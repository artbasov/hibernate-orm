/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.query.sqm.tree.expression;

import org.hibernate.query.sqm.NodeBuilder;
import org.hibernate.query.sqm.SemanticQueryWalker;
import org.hibernate.query.sqm.SqmExpressible;
import org.hibernate.query.sqm.tree.SqmCopyContext;
import org.hibernate.query.sqm.tree.SqmTypedNode;
import org.hibernate.sql.ast.tree.expression.JsonObjectAggUniqueKeysBehavior;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Specifies if a {@code json_objectagg} may aggregate duplicate keys.
 *
 * @since 7.0
 */
public enum SqmJsonObjectAggUniqueKeysBehavior implements SqmTypedNode<Object> {
	/**
	 * Aggregate only unique keys. Fail aggregation if a duplicate is encountered.
	 */
	WITH,
	/**
	 * Aggregate duplicate keys without failing.
	 */
	WITHOUT;

	@Override
	public @Nullable SqmExpressible<Object> getNodeType() {
		return null;
	}

	@Override
	public NodeBuilder nodeBuilder() {
		throw new UnsupportedOperationException();
	}

	@Override
	public SqmJsonObjectAggUniqueKeysBehavior copy(SqmCopyContext context) {
		return this;
	}

	@Override
	public <X> X accept(SemanticQueryWalker<X> walker) {
		//noinspection unchecked
		return (X) (this == WITH ? JsonObjectAggUniqueKeysBehavior.WITH : JsonObjectAggUniqueKeysBehavior.WITHOUT);
	}

	@Override
	public void appendHqlString(StringBuilder sb) {
		if ( this == WITH ) {
			sb.append( " with unique keys" );
		}
		else {
			sb.append( " without unique keys" );
		}
	}
}