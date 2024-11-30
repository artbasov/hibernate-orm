/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.hql;


/**
 * Implementation of KeyManyToOneKeyEntity.
 *
 * @author Steve Ebersole
 */
public class KeyManyToOneKeyEntity {
	private Long id;
	private String name;

	public KeyManyToOneKeyEntity(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}