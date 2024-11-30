/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.loader.ast.spi;

import org.hibernate.metamodel.mapping.EntityMappingType;

/**
 * Specialization of Loader for loading entities of a type
 *
 * @author Steve Ebersole
 */
public interface EntityLoader extends Loader {
	@Override
	EntityMappingType getLoadable();
}