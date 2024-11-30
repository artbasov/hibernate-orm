/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.processor.test.embeddable.genericsinheritance;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class ExampleEntity {
	private int id;

	private ExampleEmbedded<?> exampleEmbedded;

	@Id
	@GeneratedValue
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Embedded
	public ExampleEmbedded<?> getExampleEmbedded() {
		return exampleEmbedded;
	}
	public void setExampleEmbedded(ExampleEmbedded<?> exampleEmbedded) {
		this.exampleEmbedded = exampleEmbedded;
	}
}