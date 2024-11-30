/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.dialect.function.json;

import org.hibernate.query.sqm.function.AbstractSqmSelfRenderingFunctionDescriptor;
import org.hibernate.query.sqm.function.FunctionKind;
import org.hibernate.query.sqm.produce.function.ArgumentTypesValidator;
import org.hibernate.query.sqm.produce.function.FunctionParameterType;
import org.hibernate.query.sqm.produce.function.StandardArgumentsValidators;
import org.hibernate.query.sqm.produce.function.StandardFunctionReturnTypeResolvers;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.spi.TypeConfiguration;

/**
 * Standard json_array_insert function.
 */
public abstract class AbstractJsonArrayInsertFunction extends AbstractSqmSelfRenderingFunctionDescriptor {

	public AbstractJsonArrayInsertFunction(TypeConfiguration typeConfiguration) {
		super(
				"json_array_insert",
				FunctionKind.NORMAL,
				new ArgumentTypesValidator(
						StandardArgumentsValidators.exactly( 3 ),
						FunctionParameterType.IMPLICIT_JSON,
						FunctionParameterType.STRING,
						FunctionParameterType.ANY
				),
				StandardFunctionReturnTypeResolvers.invariant(
						typeConfiguration.getBasicTypeRegistry().resolve( String.class, SqlTypes.JSON )
				),
				null
		);
	}

}