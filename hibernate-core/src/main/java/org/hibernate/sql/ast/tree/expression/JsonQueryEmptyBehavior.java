/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.sql.ast.tree.expression;

import org.hibernate.sql.ast.SqlAstWalker;
import org.hibernate.sql.ast.tree.SqlAstNode;

/**
 * @since 7.0
 */
public enum JsonQueryEmptyBehavior implements SqlAstNode {
	ERROR,
	NULL,
	EMPTY_ARRAY,
	EMPTY_OBJECT;

	@Override
	public void accept(SqlAstWalker sqlTreeWalker) {
		throw new UnsupportedOperationException("JsonQueryEmptyBehavior doesn't support walking");
	}

}