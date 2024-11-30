/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.hbm.mappingexception;

import org.hibernate.MappingException;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;

import org.hibernate.testing.orm.junit.JiraKey;
import org.hibernate.testing.util.ServiceRegistryUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UnmappedAssociationExceptioTest {

	@Test
	@JiraKey( value = "HHH-15354")
	public void mappingExceptionTest() {
		StandardServiceRegistry ssr = ServiceRegistryUtil.serviceRegistry();
		try {
			assertThrows( MappingException.class, () -> {
				new MetadataSources( ssr )
						.addResource( "org/hibernate/orm/test/hbm/mappingexception/unmapped_association.hbm.xml" )
						.buildMetadata();

			} );
		}
		finally {
			ssr.close();
		}

	}
}