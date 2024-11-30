/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.softdelete.pkg;

import org.hibernate.metamodel.spi.MappingMetamodelImplementor;
import org.hibernate.orm.test.softdelete.MappingVerifier;

import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

/**
 * @author Steve Ebersole
 */
@DomainModel( annotatedClasses = AnEntity.class )
@SessionFactory
public class PackageLevelSoftDeleteTests {
	@Test
	public void verifyEntitySchema(SessionFactoryScope scope) {
		final MappingMetamodelImplementor metamodel = scope.getSessionFactory().getMappingMetamodel();
		MappingVerifier.verifyMapping(
				metamodel.getEntityDescriptor( AnEntity.class ).getSoftDeleteMapping(),
				"deleted",
				"the_table",
				'Y'
		);
	}

	@Test
	public void verifyCollectionSchema(SessionFactoryScope scope) {
		final MappingMetamodelImplementor metamodel = scope.getSessionFactory().getMappingMetamodel();
		MappingVerifier.verifyMapping(
				metamodel.getCollectionDescriptor( AnEntity.class.getName() + ".elements" ).getAttributeMapping().getSoftDeleteMapping(),
				"deleted",
				"elements",
				'Y'
		);
	}
}