/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.processor.test.hhh17613;

import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class ChildB<A> extends Parent {
}