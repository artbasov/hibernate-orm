/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.type.descriptor.jdbc.internal;

import org.hibernate.dialect.Dialect;
import org.hibernate.sql.ast.spi.SqlAppender;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.descriptor.jdbc.JdbcLiteralFormatter;
import org.hibernate.type.descriptor.jdbc.spi.BasicJdbcLiteralFormatter;

public class JdbcLiteralFormatterArray<T> extends BasicJdbcLiteralFormatter<T> {

	private final JdbcLiteralFormatter<Object> elementFormatter;

	public JdbcLiteralFormatterArray(JavaType<T> javaType, JdbcLiteralFormatter<?> elementFormatter) {
		super( javaType );
		//noinspection unchecked
		this.elementFormatter = (JdbcLiteralFormatter<Object>) elementFormatter;
	}

	@Override
	public void appendJdbcLiteral(SqlAppender appender, Object value, Dialect dialect, WrapperOptions wrapperOptions) {
		dialect.appendArrayLiteral(
				appender,
				unwrap( value, Object[].class, wrapperOptions ),
				elementFormatter,
				wrapperOptions
		);
	}
}