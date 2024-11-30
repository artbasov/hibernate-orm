/*
 * SPDX-License-Identifier: LGPL-2.1-or-later
 * Copyright Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.orm.test.inheritance;

import java.math.BigDecimal;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import org.hibernate.orm.test.jpa.BaseEntityManagerFunctionalTestCase;

import org.junit.Test;

import static org.hibernate.testing.transaction.TransactionUtil.doInJPA;

/**
 * @author Vlad Mihalcea
 */
public class MappedSuperclassTest extends BaseEntityManagerFunctionalTestCase {

	@Override
	protected Class<?>[] getAnnotatedClasses() {
		return new Class<?>[] {
				DebitAccount.class,
				CreditAccount.class,
		};
	}

	@Test
	public void test() {
		doInJPA(this::entityManagerFactory, entityManager -> {
			DebitAccount debitAccount = new DebitAccount();
			debitAccount.setId(1L);
			debitAccount.setOwner("John Doe");
			debitAccount.setBalance(BigDecimal.valueOf(100));
			debitAccount.setInterestRate(BigDecimal.valueOf(1.5d));
			debitAccount.setOverdraftFee(BigDecimal.valueOf(25));

			CreditAccount creditAccount = new CreditAccount();
			creditAccount.setId(1L);
			creditAccount.setOwner("John Doe");
			creditAccount.setBalance(BigDecimal.valueOf(1000));
			creditAccount.setInterestRate(BigDecimal.valueOf(1.9d));
			creditAccount.setCreditLimit(BigDecimal.valueOf(5000));

			entityManager.persist(debitAccount);
			entityManager.persist(creditAccount);
		});
	}

	//tag::entity-inheritance-mapped-superclass-example[]
	@MappedSuperclass
	public static class Account {

		@Id
		private Long id;

		private String owner;

		private BigDecimal balance;

		private BigDecimal interestRate;

		//Getters and setters are omitted for brevity

	//end::entity-inheritance-mapped-superclass-example[]

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getOwner() {
			return owner;
		}

		public void setOwner(String owner) {
			this.owner = owner;
		}

		public BigDecimal getBalance() {
			return balance;
		}

		public void setBalance(BigDecimal balance) {
			this.balance = balance;
		}

		public BigDecimal getInterestRate() {
			return interestRate;
		}

		public void setInterestRate(BigDecimal interestRate) {
			this.interestRate = interestRate;
		}
	//tag::entity-inheritance-mapped-superclass-example[]
	}

	@Entity(name = "DebitAccount")
	public static class DebitAccount extends Account {

		private BigDecimal overdraftFee;

		//Getters and setters are omitted for brevity

	//end::entity-inheritance-mapped-superclass-example[]

		public BigDecimal getOverdraftFee() {
			return overdraftFee;
		}

		public void setOverdraftFee(BigDecimal overdraftFee) {
			this.overdraftFee = overdraftFee;
		}
	//tag::entity-inheritance-mapped-superclass-example[]
	}

	@Entity(name = "CreditAccount")
	public static class CreditAccount extends Account {

		private BigDecimal creditLimit;

		//Getters and setters are omitted for brevity

	//end::entity-inheritance-mapped-superclass-example[]

		public BigDecimal getCreditLimit() {
			return creditLimit;
		}

		public void setCreditLimit(BigDecimal creditLimit) {
			this.creditLimit = creditLimit;
		}
	//tag::entity-inheritance-mapped-superclass-example[]
	}
	//end::entity-inheritance-mapped-superclass-example[]
}