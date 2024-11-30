/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.mapping.mappedBy;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import org.hibernate.Hibernate;
import org.hibernate.annotations.ListIndexBase;
import org.hibernate.testing.orm.junit.DomainModel;
import org.hibernate.testing.orm.junit.SessionFactory;
import org.hibernate.testing.orm.junit.SessionFactoryScope;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SessionFactory
@DomainModel(annotatedClasses = {MappedByNonAssociationTest.Loan.class, MappedByNonAssociationTest.Extensions.class})
public class MappedByNonAssociationTest {

	@Test void test(SessionFactoryScope scope) {
		Extensions ex = new Extensions();
		ex.exExtensionDays = 3L;
		ex.exNo = 1L;
		ex.exLoanId = 4L;

		Loan loan = new Loan();
		loan.id = 4L;
		loan.extensions.add(ex);

		scope.inTransaction(s -> s.persist(loan));
		Loan l1 = scope.fromTransaction(s -> {
			Loan ll = s.get(Loan.class, loan.id);
			Hibernate.initialize(ll.extensions);
			return ll;
		});
		assertEquals( 1, l1.extensions.size() );
		assertEquals( loan.id, l1.id );
		assertEquals( ex.exLoanId, l1.extensions.get(0).exLoanId );
		Loan l2 = scope.fromSession(s -> s.createQuery("from Loan join fetch extensions", Loan.class).getSingleResult());
		assertEquals( 1, l2.extensions.size() );
		assertEquals( loan.id, l2.id );
		assertEquals( ex.exLoanId, l2.extensions.get(0).exLoanId );
	}

	@Entity(name="Loan")
	static class Loan {
		@Id
		@Column(name = "LOAN_ID")
		private Long id;

		@OneToMany(cascade = PERSIST, mappedBy = "exLoanId")
		@OrderColumn(name = "EX_NO") @ListIndexBase(1)
		private List<Extensions> extensions = new ArrayList<>();
	}

	@Entity(name="Extensions")
	static class Extensions  {
		@Id
		@Column(name = "EX_LOAN_ID")
		private Long exLoanId;
		@Id
		@Column(name = "EX_NO")
		private Long exNo;
		@Column(name = "EX_EXTENSION_DAYS")
		private Long exExtensionDays;
	}

}