/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.processor.test.util;

import org.junit.After;
import org.junit.runner.RunWith;

/**
 * Base class for annotation processor tests.
 *
 * @author Hardy Ferentschik
 */
@RunWith(CompilationRunner.class)
public abstract class CompilationTest {

	public CompilationTest() {
	}

	@After
	public void cleanup() throws Exception {
		TestUtil.deleteProcessorGeneratedFiles(getClass());
	}
}