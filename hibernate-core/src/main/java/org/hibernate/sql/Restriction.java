/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.sql;

import org.hibernate.Internal;

/**
 * A restriction (predicate) to be applied to a query
 *
 * @author Steve Ebersole
 */
@Internal
public interface Restriction {
	/**
	 * Render the restriction into the SQL buffer
	 */
	void render(StringBuilder sqlBuffer, RestrictionRenderingContext context);
}