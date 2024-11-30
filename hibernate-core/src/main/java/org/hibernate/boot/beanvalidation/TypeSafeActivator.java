/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.beanvalidation;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.AssertionFailure;
import org.hibernate.MappingException;
import org.hibernate.boot.internal.ClassLoaderAccessImpl;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.config.spi.StandardConverters;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.mapping.CheckConstraint;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.SingleTableSubclass;

import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.jboss.logging.Logger;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.metadata.BeanDescriptor;
import jakarta.validation.metadata.ConstraintDescriptor;
import jakarta.validation.metadata.PropertyDescriptor;

import static java.util.Arrays.asList;
import static java.util.Collections.disjoint;
import static org.hibernate.boot.beanvalidation.BeanValidationIntegrator.APPLY_CONSTRAINTS;
import static org.hibernate.boot.beanvalidation.GroupsPerOperation.buildGroupsForOperation;
import static org.hibernate.cfg.ValidationSettings.CHECK_NULLABILITY;
import static org.hibernate.cfg.ValidationSettings.JAKARTA_VALIDATION_FACTORY;
import static org.hibernate.cfg.ValidationSettings.JPA_VALIDATION_FACTORY;
import static org.hibernate.internal.util.StringHelper.isEmpty;
import static org.hibernate.internal.util.StringHelper.isNotEmpty;

/**
 * Sets up Bean Validation {@linkplain BeanValidationEventListener event listener} and DDL-based constraints.
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 * @author Steve Ebersole
 */
class TypeSafeActivator {

	private static final CoreMessageLogger LOG = Logger.getMessageLogger( MethodHandles.lookup(), CoreMessageLogger.class, TypeSafeActivator.class.getName() );

	/**
	 * Used to validate a supplied ValidatorFactory instance as being castable to ValidatorFactory.
	 *
	 * @param object The supplied ValidatorFactory instance.
	 */
	@SuppressWarnings("unused")
	public static void validateSuppliedFactory(Object object) {
		if ( !(object instanceof ValidatorFactory) ) {
			throw new IntegrationException( "Given object was not an instance of " + ValidatorFactory.class.getName()
							+ " [" + object.getClass().getName() + "]" );
		}
	}

	@SuppressWarnings("unused")
	public static void activate(ActivationContext context) {
		final ValidatorFactory factory;
		try {
			factory = getValidatorFactory( context );
		}
		catch (IntegrationException e) {
			final Set<ValidationMode> validationModes = context.getValidationModes();
			if ( validationModes.contains( ValidationMode.CALLBACK ) ) {
				throw new IntegrationException( "Bean Validation provider was not available, but 'callback' validation was requested", e );
			}
			else if ( validationModes.contains( ValidationMode.DDL ) ) {
				throw new IntegrationException( "Bean Validation provider was not available, but 'ddl' validation was requested", e );
			}
			else {
				LOG.debug( "Unable to acquire Bean Validation ValidatorFactory, skipping activation" );
				return;
			}
		}

		applyRelationalConstraints( factory, context );
		applyCallbackListeners( factory, context );
	}

	public static void applyCallbackListeners(ValidatorFactory validatorFactory, ActivationContext context) {
		if ( isValidationEnabled( context ) ) {
			disableNullabilityChecking( context );
			setupListener( validatorFactory, context.getServiceRegistry() );
		}
	}

	private static boolean isValidationEnabled(ActivationContext context) {
		final Set<ValidationMode> modes = context.getValidationModes();
		return modes.contains( ValidationMode.CALLBACK )
			|| modes.contains( ValidationMode.AUTO );
	}

	/**
	 * Deactivate not-null tracking at the core level when Bean Validation
	 * is present, unless the user explicitly asks for it.
	 */
	private static void disableNullabilityChecking(ActivationContext context) {
		if ( isCheckNullabilityExplicit( context ) ) {
			// no explicit setting, so disable null checking
			context.getSessionFactory().getSessionFactoryOptions().setCheckNullability( false );
		}
	}

	private static boolean isCheckNullabilityExplicit(ActivationContext context) {
		return context.getServiceRegistry().requireService( ConfigurationService.class )
				.getSettings().get( CHECK_NULLABILITY ) == null;
	}

	private static void setupListener(ValidatorFactory validatorFactory, SessionFactoryServiceRegistry serviceRegistry) {
		final ClassLoaderService classLoaderService = serviceRegistry.requireService( ClassLoaderService.class );
		final ConfigurationService cfgService = serviceRegistry.requireService( ConfigurationService.class );
		final BeanValidationEventListener listener =
				new BeanValidationEventListener( validatorFactory, cfgService.getSettings(), classLoaderService );
		final EventListenerRegistry listenerRegistry = serviceRegistry.requireService( EventListenerRegistry.class );
		listenerRegistry.addDuplicationStrategy( DuplicationStrategyImpl.INSTANCE );
		listenerRegistry.appendListeners( EventType.PRE_INSERT, listener );
		listenerRegistry.appendListeners( EventType.PRE_UPDATE, listener );
		listenerRegistry.appendListeners( EventType.PRE_DELETE, listener );
		listenerRegistry.appendListeners( EventType.PRE_UPSERT, listener );
		listener.initialize( cfgService.getSettings(), classLoaderService );
	}

	private static boolean isConstraintBasedValidationEnabled(ActivationContext context) {
		if ( context.getServiceRegistry().requireService( ConfigurationService.class )
				.getSetting( APPLY_CONSTRAINTS, StandardConverters.BOOLEAN, true ) ) {
			final Set<ValidationMode> modes = context.getValidationModes();
			return modes.contains( ValidationMode.DDL )
				|| modes.contains( ValidationMode.AUTO );
		}
		else {
			LOG.debug( "Skipping application of relational constraints from legacy Hibernate Validator" );
			return false;
		}
	}

	private static void applyRelationalConstraints(ValidatorFactory factory, ActivationContext context) {
		if ( isConstraintBasedValidationEnabled( context ) ) {
			final SessionFactoryServiceRegistry serviceRegistry = context.getServiceRegistry();
			applyRelationalConstraints(
					factory,
					context.getMetadata().getEntityBindings(),
					serviceRegistry.requireService( ConfigurationService.class ).getSettings(),
					serviceRegistry.requireService( JdbcServices.class ).getDialect(),
					new ClassLoaderAccessImpl( null, serviceRegistry.getService( ClassLoaderService.class ) )
			);
		}
	}

	public static void applyRelationalConstraints(
			ValidatorFactory factory,
			Collection<PersistentClass> persistentClasses,
			Map<String,Object> settings,
			Dialect dialect,
			ClassLoaderAccess classLoaderAccess) {
		final Class<?>[] groupsArray =
				buildGroupsForOperation( GroupsPerOperation.Operation.DDL, settings, classLoaderAccess );
		final Set<Class<?>> groups = new HashSet<>( asList( groupsArray ) );
		for ( PersistentClass persistentClass : persistentClasses ) {
			final String className = persistentClass.getClassName();
			if ( isNotEmpty( className ) ) {
				final Class<?> clazz = entityClass( classLoaderAccess, className );
				try {
					applyDDL( "", persistentClass, clazz, factory, groups, true, dialect );
				}
				catch (Exception e) {
					LOG.unableToApplyConstraints( className, e );
				}
			}
		}
	}

	private static Class<?> entityClass(ClassLoaderAccess classLoaderAccess, String className) {
		try {
			return classLoaderAccess.classForName( className );
		}
		catch (ClassLoadingException e) {
			throw new AssertionFailure( "Entity class not found", e );
		}
	}

	private static void applyDDL(
			String prefix,
			PersistentClass persistentClass,
			Class<?> clazz,
			ValidatorFactory factory,
			Set<Class<?>> groups,
			boolean activateNotNull,
			Dialect dialect) {
		final BeanDescriptor descriptor = factory.getValidator().getConstraintsForClass( clazz );
		//cno bean level constraints can be applied, go to the properties
		for ( PropertyDescriptor propertyDesc : descriptor.getConstrainedProperties() ) {
			final Property property =
					findPropertyByName( persistentClass, prefix + propertyDesc.getPropertyName() );
			if ( property != null ) {
				final boolean hasNotNull = applyConstraints(
						propertyDesc.getConstraintDescriptors(),
						property,
						propertyDesc,
						groups,
						activateNotNull,
						dialect
				);
				if ( property.isComposite() && propertyDesc.isCascaded() ) {
					final Component component = (Component) property.getValue();
					applyDDL(
							prefix + propertyDesc.getPropertyName() + ".",
							persistentClass,
							component.getComponentClass(),
							factory,
							groups,
							// we can apply not null if the upper component lets us
							// activate not null and if the property is not null.
							// Otherwise, all sub columns should be left nullable
							activateNotNull && hasNotNull,
							dialect
					);
				}
			}
		}
	}

	private static boolean applyConstraints(
			Set<ConstraintDescriptor<?>> constraintDescriptors,
			Property property,
			PropertyDescriptor propertyDesc,
			Set<Class<?>> groups,
			boolean canApplyNotNull,
			Dialect dialect) {
		boolean hasNotNull = false;
		for ( ConstraintDescriptor<?> descriptor : constraintDescriptors ) {
			if ( groups == null || !disjoint( descriptor.getGroups(), groups ) ) {
				if ( canApplyNotNull ) {
					hasNotNull = hasNotNull || applyNotNull( property, descriptor );
				}

				// apply bean validation specific constraints
				applyDigits( property, descriptor );
				applySize( property, descriptor, propertyDesc );
				applyMin( property, descriptor, dialect );
				applyMax( property, descriptor, dialect );

				// Apply Hibernate Validator specific constraints - we cannot import any HV specific classes though!
				// No need to check explicitly for @Range. @Range is a composed constraint using @Min and @Max which
				// will be taken care later.
				applyLength( property, descriptor, propertyDesc );

				// pass an empty set as composing constraints inherit the main constraint and thus are matching already
				final boolean hasNotNullFromComposingConstraints = applyConstraints(
						descriptor.getComposingConstraints(),
						property, propertyDesc, null,
						canApplyNotNull,
						dialect
				);

				hasNotNull = hasNotNull || hasNotNullFromComposingConstraints;
			}

		}
		return hasNotNull;
	}

	private static void applyMin(Property property, ConstraintDescriptor<?> descriptor, Dialect dialect) {
		if ( Min.class.equals( descriptor.getAnnotation().annotationType() ) ) {
			@SuppressWarnings("unchecked")
			final ConstraintDescriptor<Min> minConstraint = (ConstraintDescriptor<Min>) descriptor;
			final long min = minConstraint.getAnnotation().value();
			for ( Selectable selectable : property.getSelectables() ) {
				if ( selectable instanceof Column column ) {
					applySQLCheck( column, column.getQuotedName( dialect ) + ">=" + min );
				}
			}
		}
	}

	private static void applyMax(Property property, ConstraintDescriptor<?> descriptor, Dialect dialect) {
		if ( Max.class.equals( descriptor.getAnnotation().annotationType() ) ) {
			@SuppressWarnings("unchecked")
			final ConstraintDescriptor<Max> maxConstraint = (ConstraintDescriptor<Max>) descriptor;
			final long max = maxConstraint.getAnnotation().value();
			for ( Selectable selectable : property.getSelectables() ) {
				if ( selectable instanceof Column column ) {
					applySQLCheck( column, column.getQuotedName( dialect ) + "<=" + max );
				}
			}
		}
	}

	private static void applySQLCheck(Column column, String checkConstraint) {
		// need to check whether the new check is already part of the existing check,
		// because applyDDL can be called multiple times
		for ( CheckConstraint constraint : column.getCheckConstraints() ) {
			if ( constraint.getConstraint().equalsIgnoreCase( checkConstraint ) ) {
				return; //EARLY EXIT
			}
		}
		column.addCheckConstraint( new CheckConstraint( checkConstraint ) );
	}

	private static boolean applyNotNull(Property property, ConstraintDescriptor<?> descriptor) {
		boolean hasNotNull = false;
		// NotNull, NotEmpty, and NotBlank annotation add not-null on column
		final Class<? extends Annotation> annotationType = descriptor.getAnnotation().annotationType();
		if ( NotNull.class.equals(annotationType)
				|| NotEmpty.class.equals(annotationType)
				|| NotBlank.class.equals(annotationType)) {
			// single table inheritance should not be forced to null due to shared state
			if ( !( property.getPersistentClass() instanceof SingleTableSubclass ) ) {
				// composite should not add not-null on all columns
				if ( !property.isComposite() ) {
					for ( Selectable selectable : property.getSelectables() ) {
						if ( selectable instanceof Column column ) {
							column.setNullable( false );
						}
						else {
							LOG.debugf(
									"@NotNull was applied to attribute [%s] which is defined (at least partially) " +
											"by formula(s); formula portions will be skipped",
									property.getName()
							);
						}
					}
				}
			}
			hasNotNull = true;
		}
		property.setOptional( !hasNotNull );
		return hasNotNull;
	}

	private static void applyDigits(Property property, ConstraintDescriptor<?> descriptor) {
		if ( Digits.class.equals( descriptor.getAnnotation().annotationType() ) ) {
			@SuppressWarnings("unchecked")
			final ConstraintDescriptor<Digits> digitsConstraint = (ConstraintDescriptor<Digits>) descriptor;
			final int integerDigits = digitsConstraint.getAnnotation().integer();
			final int fractionalDigits = digitsConstraint.getAnnotation().fraction();
			for ( Selectable selectable : property.getSelectables() ) {
				if ( selectable instanceof Column column ) {
					column.setPrecision( integerDigits + fractionalDigits );
					column.setScale( fractionalDigits );
				}
			}

		}
	}

	private static void applySize(Property property, ConstraintDescriptor<?> descriptor, PropertyDescriptor propertyDescriptor) {
		if ( Size.class.equals( descriptor.getAnnotation().annotationType() )
				&& String.class.equals( propertyDescriptor.getElementClass() ) ) {
			@SuppressWarnings("unchecked")
			final ConstraintDescriptor<Size> sizeConstraint = (ConstraintDescriptor<Size>) descriptor;
			final int max = sizeConstraint.getAnnotation().max();
			for ( Column col : property.getColumns() ) {
				if ( max < Integer.MAX_VALUE ) {
					col.setLength( max );
				}
			}
		}
	}

	private static void applyLength(Property property, ConstraintDescriptor<?> descriptor, PropertyDescriptor propertyDescriptor) {
		if ( isValidatorLengthAnnotation( descriptor )
				&& String.class.equals( propertyDescriptor.getElementClass() ) ) {
			final int max = (Integer) descriptor.getAttributes().get( "max" );
			for ( Selectable selectable : property.getSelectables() ) {
				if ( selectable instanceof Column column ) {
					if ( max < Integer.MAX_VALUE ) {
						column.setLength( max );
					}
				}
			}
		}
	}

	private static boolean isValidatorLengthAnnotation(ConstraintDescriptor<?> descriptor) {
		return "org.hibernate.validator.constraints.Length"
				.equals( descriptor.getAnnotation().annotationType().getName() );
	}

	/**
	 * Locate the property by path in a recursive way, including IdentifierProperty in the loop if propertyName is
	 * {@code null}.  If propertyName is {@code null} or empty, the IdentifierProperty is returned
	 */
	private static Property findPropertyByName(PersistentClass associatedClass, String propertyName) {
		Property property = null;
		final Property idProperty = associatedClass.getIdentifierProperty();
		final String idName = idProperty != null ? idProperty.getName() : null;
		try {
			if ( isEmpty( propertyName ) || propertyName.equals( idName ) ) {
				//default to id
				property = idProperty;
			}
			else {
				if ( propertyName.indexOf( idName + "." ) == 0 ) {
					property = idProperty;
					propertyName = propertyName.substring( idName.length() + 1 );
				}
				final StringTokenizer tokens = new StringTokenizer( propertyName, ".", false );
				while ( tokens.hasMoreTokens() ) {
					final String element = tokens.nextToken();
					if ( property == null ) {
						property = associatedClass.getProperty( element );
					}
					else {
						if ( !property.isComposite() ) {
							return null;
						}
						else {
							property = ( (Component) property.getValue() ).getProperty( element );
						}
					}
				}
			}
		}
		catch ( MappingException e ) {
			try {
				//if we do not find it try to check the identifier mapper
				if ( associatedClass.getIdentifierMapper() == null ) {
					return null;
				}
				else {
					final StringTokenizer tokens = new StringTokenizer( propertyName, ".", false );
					while ( tokens.hasMoreTokens() ) {
						final String element = tokens.nextToken();
						if ( property == null ) {
							property = associatedClass.getIdentifierMapper().getProperty( element );
						}
						else {
							if ( !property.isComposite() ) {
								return null;
							}
							else {
								property = ( (Component) property.getValue() ).getProperty( element );
							}
						}
					}
				}
			}
			catch ( MappingException ee ) {
				return null;
			}
		}
		return property;
	}

	private static ValidatorFactory getValidatorFactory(ActivationContext context) {
		// IMPL NOTE : We can either be provided a ValidatorFactory or make one.  We can be provided
		// a ValidatorFactory in 2 different ways.  So here we "get" a ValidatorFactory in the following order:
		//		1) Look into SessionFactoryOptions.getValidatorFactoryReference()
		//		2) Look into ConfigurationService
		//		3) build a new ValidatorFactory

		// 1 - look in SessionFactoryOptions.getValidatorFactoryReference()
		final ValidatorFactory providedFactory =
				resolveProvidedFactory( context.getSessionFactory().getSessionFactoryOptions() );
		if ( providedFactory != null ) {
			return providedFactory;
		}

		// 2 - look in ConfigurationService
		final ValidatorFactory configuredFactory =
				resolveProvidedFactory( context.getServiceRegistry().requireService( ConfigurationService.class ) );
		if ( configuredFactory != null ) {
			return configuredFactory;
		}

		// 3 - build our own
		try {
			return Validation.buildDefaultValidatorFactory();
		}
		catch ( Exception e ) {
			throw new IntegrationException( "Unable to build the default ValidatorFactory", e );
		}
	}

	private static ValidatorFactory resolveProvidedFactory(SessionFactoryOptions options) {
		final Object validatorFactoryReference = options.getValidatorFactoryReference();
		if ( validatorFactoryReference == null ) {
			return null;
		}
		else if ( validatorFactoryReference instanceof ValidatorFactory result ) {
			return result;
		}
		else {
			throw new IntegrationException(
					String.format(
							Locale.ENGLISH,
							"ValidatorFactory reference (provided via %s) was not castable to %s : %s",
							SessionFactoryOptions.class.getName(),
							ValidatorFactory.class.getName(),
							validatorFactoryReference.getClass().getName()
					)
			);
		}
	}

	private static ValidatorFactory resolveProvidedFactory(ConfigurationService cfgService) {
		return cfgService.getSetting(
				JPA_VALIDATION_FACTORY,
				value -> validatorFactory( value, JPA_VALIDATION_FACTORY ),
				cfgService.getSetting(
						JAKARTA_VALIDATION_FACTORY,
						value -> validatorFactory( value, JAKARTA_VALIDATION_FACTORY ),
						null
				)
		);
	}

	private static ValidatorFactory validatorFactory(Object value, String setting) {
		if ( value instanceof ValidatorFactory validatorFactory ) {
			return validatorFactory;
		}
		else {
			throw new IntegrationException(
					String.format(
							Locale.ENGLISH,
							"ValidatorFactory reference (provided via '%s' setting) was not an instance of '%s': %s",
							setting,
							ValidatorFactory.class.getName(),
							value.getClass().getName()
					)
			);
		}
	}
}