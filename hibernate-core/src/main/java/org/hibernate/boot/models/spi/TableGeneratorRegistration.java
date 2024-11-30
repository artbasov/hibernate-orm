/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.boot.models.spi;

import jakarta.persistence.TableGenerator;

/**
 * Global registration of a table generator
 *
 * @author Steve Ebersole
 */
public record TableGeneratorRegistration(String name, TableGenerator configuration) {
}