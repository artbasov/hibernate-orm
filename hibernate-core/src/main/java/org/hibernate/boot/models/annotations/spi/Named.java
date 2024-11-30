/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.annotations.spi;

/**
 * Commonality for annotations that are named within a {@linkplain RepeatableContainer repeatable container}.
 *
 * @author Steve Ebersole
 */
public interface Named {
	String name();

	void name(String name);
}