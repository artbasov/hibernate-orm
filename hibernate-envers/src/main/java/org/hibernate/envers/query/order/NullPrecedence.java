/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.envers.query.order;

/**
 * Defines the possible null handling modes.
 *
 * @author Chris Cranford
 */
public enum NullPrecedence {
	/**
	 * Null values will be rendered before non-null values.
	 */
	FIRST,

	/**
	 * Null values will be rendered after non-null values.
	 */
	LAST
}