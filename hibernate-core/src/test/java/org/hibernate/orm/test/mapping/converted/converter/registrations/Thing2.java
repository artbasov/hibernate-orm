/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.mapping.converted.converter.registrations;

import java.io.Serializable;

/**
 * @author Steve Ebersole
 */
public class Thing2 implements Thing, Serializable {
	private final String content;

	public Thing2(String content) {
		this.content = content;
	}

	@Override
	public String getContent() {
		return content;
	}
}