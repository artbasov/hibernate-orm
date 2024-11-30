/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.spi;

import org.hibernate.annotations.GenericGenerator;

/**
 * Global registration of a generic generator
 *
 * @author Steve Ebersole
 * @see GenericGenerator
 * @see org.hibernate.boot.jaxb.mapping.spi.JaxbGenericIdGeneratorImpl
 */
public record GenericGeneratorRegistration(String name, GenericGenerator configuration) {
}