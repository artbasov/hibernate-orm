/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.annotations.spi;

import java.lang.annotation.Annotation;

import jakarta.persistence.UniqueConstraint;

/**
 * Commonality for annotations which define unique-constraints
 *
 * @author Steve Ebersole
 */
public interface UniqueConstraintCollector extends Annotation {
	UniqueConstraint[] uniqueConstraints();

	void uniqueConstraints(UniqueConstraint[] value);
}