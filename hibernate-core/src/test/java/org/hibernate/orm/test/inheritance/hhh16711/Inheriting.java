/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.inheritance.hhh16711;

import org.hibernate.orm.test.inheritance.hhh16711.otherPackage.Inherited;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "inheriting")
class Inheriting extends Inherited {

	Inheriting(String id, String name) {
		super(id, name);
	}

	Inheriting() {
	}
}