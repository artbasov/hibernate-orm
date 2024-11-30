/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.jaxb.mapping.spi;

/**
 * @author Steve Ebersole
 */
public interface JaxbMappedSuperclass extends JaxbEntityOrMappedSuperclass {
	@Override
	JaxbAttributesContainerImpl getAttributes();
}