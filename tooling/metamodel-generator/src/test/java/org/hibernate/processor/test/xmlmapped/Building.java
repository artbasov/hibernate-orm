/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.processor.test.xmlmapped;

import org.hibernate.processor.test.accesstype.Address;
import org.hibernate.processor.test.accesstype.Area;

/**
 * @author Hardy Ferentschik
 */
public class Building extends Area {
	private Address address;

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
}