/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.processor.annotation;

class OrderBy {
	String fieldName;
	boolean descending;
	boolean ignoreCase;

	public OrderBy(String fieldName, boolean descending, boolean ignoreCase) {
		this.fieldName = fieldName;
		this.descending = descending;
		this.ignoreCase = ignoreCase;
	}
}