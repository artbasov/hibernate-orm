/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.processor.test.xmlembeddable;

import java.io.Serializable;

import org.hibernate.processor.test.xmlembeddable.foo.BusinessId;

/**
 * @author Hardy Ferentschik
 */
public abstract class BusinessEntity<T extends Serializable> implements Serializable {
	private Long id;

	private BusinessId<T> businessId;
}