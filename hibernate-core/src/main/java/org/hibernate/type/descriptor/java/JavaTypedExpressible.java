/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.type.descriptor.java;

import org.hibernate.cache.MutableCacheKeyBuilder;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

/**
 * Contract for something that has an associated JavaType
 */
public interface JavaTypedExpressible<T> {
	JavaType<T> getExpressibleJavaType();

	void addToCacheKey(MutableCacheKeyBuilder cacheKey, Object value, SharedSessionContractImplementor session);

}