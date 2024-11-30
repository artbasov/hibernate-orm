/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate;

/**
 * Thrown when user code causes the end of a transaction that is
 * being managed by the {@link SessionFactory}.
 *
 * @see SessionFactory#inTransaction
 * @see SessionFactory#fromTransaction
 */
public class TransactionManagementException extends RuntimeException {
	public TransactionManagementException(String message) {
		super( message );
	}
}