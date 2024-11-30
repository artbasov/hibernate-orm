/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.processor.test.collectionbasictype;

import jakarta.persistence.AttributeConverter;

/**
 * @author Chris Cranford
 */
public class StringToUppercaseConverter implements AttributeConverter<String, String> {
	@Override
	public String convertToDatabaseColumn(String s) {
		return s == null ? null : s.toUpperCase();
	}

	@Override
	public String convertToEntityAttribute(String s) {
		return s;
	}
}