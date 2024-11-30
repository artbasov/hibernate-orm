/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.mapping.type.java;

import java.util.Date;

import org.hibernate.type.descriptor.java.JdbcTimestampJavaType;

/**
 * @author Owen Farrell
 */
public class JdbcTimestampJavaTypeTest extends AbstractDescriptorTest<Date> {
	final Date original = new Date();
	final Date copy = new Date( original.getTime() );
	final Date different = new Date( original.getTime() + 500L);

	public JdbcTimestampJavaTypeTest() {
		super( JdbcTimestampJavaType.INSTANCE );
	}

	@Override
	protected Data<Date> getTestData() {
		return new Data<Date>( original, copy, different );
	}

	@Override
	protected boolean shouldBeMutable() {
		return true;
	}
}