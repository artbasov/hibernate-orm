/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.processor.test.embeddable.nested.field;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Author {
	@Id
	String ssn;

	@Basic(optional = false)
	String name;

	Address address;

	Boolean deceased;
}