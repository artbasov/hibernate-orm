/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.type.descriptor.jdbc;

import org.hibernate.dialect.MySQLDialect;

import static org.hibernate.type.SqlTypes.ORDINAL_ENUM;

/**
 * Represents an {@code enum} type for databases like MySQL and H2.
 * <p>
 * Hibernate will automatically use this for enums mapped
 * as {@link jakarta.persistence.EnumType#ORDINAL}.
 *
 * @see org.hibernate.type.SqlTypes#ORDINAL_ENUM
 * @see MySQLDialect#getEnumTypeDeclaration(String, String[])
 */
public class OrdinalEnumJdbcType extends EnumJdbcType {

	public static final OrdinalEnumJdbcType INSTANCE = new OrdinalEnumJdbcType();

	@Override
	public int getDefaultSqlTypeCode() {
		return ORDINAL_ENUM;
	}

}