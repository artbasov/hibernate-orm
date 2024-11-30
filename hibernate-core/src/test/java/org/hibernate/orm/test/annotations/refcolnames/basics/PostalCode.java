/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.annotations.refcolnames.basics;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.MappedSuperclass;

@Embeddable @MappedSuperclass
class PostalCode {
	@Column(name="country_code", nullable = false)
	String countryCode;
	@Column(name="zip_code", nullable = false)
	int zipCode;
}