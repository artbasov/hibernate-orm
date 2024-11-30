/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.annotations.spi;

/**
 * Information which is common across all table annotations
 *
 * @author Steve Ebersole
 */
public interface CommonTableDetails extends DatabaseObjectDetails, UniqueConstraintCollector, IndexCollector {
	/**
	 * The table name
	 */
	String name();

	/**
	 * Setter for {@linkplain #name()}
	 */
	void name(String name);
}