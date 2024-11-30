/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.annotations.internal;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.annotations.UuidGenerator;
import org.hibernate.id.uuid.UuidValueGenerator;
import org.hibernate.models.spi.SourceModelBuildingContext;

@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
@jakarta.annotation.Generated("org.hibernate.orm.build.annotations.ClassGeneratorProcessor")
public class UuidGeneratorAnnotation implements UuidGenerator {
	private org.hibernate.annotations.UuidGenerator.Style style;
	private Class<? extends UuidValueGenerator> algorithm;

	/**
	 * Used in creating dynamic annotation instances (e.g. from XML)
	 */
	public UuidGeneratorAnnotation(SourceModelBuildingContext modelContext) {
		this.style = org.hibernate.annotations.UuidGenerator.Style.AUTO;
		this.algorithm = UuidValueGenerator.class;
	}

	/**
	 * Used in creating annotation instances from JDK variant
	 */
	public UuidGeneratorAnnotation(UuidGenerator annotation, SourceModelBuildingContext modelContext) {
		this.style = annotation.style();
		this.algorithm = annotation.algorithm();
	}

	/**
	 * Used in creating annotation instances from Jandex variant
	 */
	public UuidGeneratorAnnotation(Map<String, Object> attributeValues, SourceModelBuildingContext modelContext) {
		this.style = (Style) attributeValues.get( "style" );
		this.algorithm = (Class<? extends UuidValueGenerator>) attributeValues.get( "algorithm" );
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return UuidGenerator.class;
	}

	@Override
	public org.hibernate.annotations.UuidGenerator.Style style() {
		return style;
	}

	@Override
	public Class<? extends UuidValueGenerator> algorithm() {
		return algorithm;
	}

	public void style(org.hibernate.annotations.UuidGenerator.Style value) {
		this.style = value;
	}

}