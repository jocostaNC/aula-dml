package pt.ulisboa.tecnico.softeng.bank.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Test;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class BankPersistenceTest {
	@Test
	public void success() {
		atomicProcess();
		atomicAssert();
	}

	@Atomic(mode = TxMode.WRITE)
	public void atomicProcess() {
		new Bank("Money", "BK01");
	}

	@Atomic(mode = TxMode.READ)
	public void atomicAssert() {
		Bank bank = Bank.getBankByCode("BK01");

		assertEquals("Money", bank.getName());
	}
	
	@Test
	public void successAccount() {
		atomicProcess();
		atomicAccountProcess();
		atomicAccountAssert();
	}
	
	@Atomic(mode = TxMode.WRITE)
	public void atomicAccountProcess() {
		new Account(123456789, 100, Bank.getBankByCode("BK01"));
		new Account(123456788, 200, Bank.getBankByCode("BK01"));
		new Account(123456777, 300, Bank.getBankByCode("BK01"));
	}
	
	@Atomic(mode = TxMode.READ)
	public void atomicAccountAssert() {
		int numberAccounts = Bank.getBankByCode("BK01").getAccountCount();
		assertEquals(numberAccounts, 3);
		for(Account account : Bank.getBankByCode("BK01").getAccountSet()) {
			if(account.getIban() == 123456789) {
				assertEquals(account.getAmount(), 100);
				return;
			}
		}
		fail();
	}
	
	@Test
	public void successDeposit() {
		atomicProcess();
		atomicAccountProcess();
		atomicDepositProcess();
		atomicDepositAssert();
	}
	
	@Atomic(mode = TxMode.WRITE)
	public void atomicDepositProcess() {
		Account account = Account.getAccountByIban(123456789, "BK01");
		account.deposit(-10);
		account = Account.getAccountByIban(123456788, "BK01");
		account.deposit(10);
	}
	
	@Atomic(mode = TxMode.READ)
	public void atomicDepositAssert() {
		Account account = Account.getAccountByIban(123456789, "BK01");
		assertEquals(100, account.getAmount());
		account = Account.getAccountByIban(123456788, "BK01");
		assertEquals(210, account.getAmount());
	}
	
	@Test
	public void successWithDraw() {
		atomicProcess();
		atomicAccountProcess();
		atomicWithDrawProcess();
		atomicWithDrawAssert();
	}
	
	@Atomic(mode = TxMode.WRITE)
	public void atomicWithDrawProcess() {
		Account account = Account.getAccountByIban(123456789, "BK01");
		account.withdraw(101);
		account = Account.getAccountByIban(123456788, "BK01");
		account.withdraw(10);
	}
	
	@Atomic(mode = TxMode.READ)
	public void atomicWithDrawAssert() {
		Account account = Account.getAccountByIban(123456789, "BK01");
		assertEquals(100, account.getAmount());
		account = Account.getAccountByIban(123456788, "BK01");
		assertEquals(190, account.getAmount());
	}
	
	@Test
	public void successTotalBalance() {
		atomicProcess();
		atomicAccountProcess();
		atomicTotalBalanceAssert();
	}
	
	@Atomic(mode = TxMode.READ)
	public void atomicTotalBalanceAssert() {
		int total = Bank.getBankByCode("BK01").totalBalance();
		assertEquals(600, total);
	}
	
	
	@Test
	public void successSavingsAccountDeposit() {
		atomicProcess();
		atomicSavingsAccountProcess();
		atomicSavingsAccountDepositProcess();
		atomicSavingsAccountDepositAssert();
	}
	
	@Atomic(mode = TxMode.WRITE) 
	public void atomicSavingsAccountProcess() {
		new SavingsAccount(123456789, 100, Bank.getBankByCode("BK01"));
		new SavingsAccount(123456788, 200, Bank.getBankByCode("BK01"));
		new SavingsAccount(123456777, 300, Bank.getBankByCode("BK01"));
	}
	
	@Atomic(mode = TxMode.WRITE)
	public void atomicSavingsAccountDepositProcess() {
		SavingsAccount savingsAccount = SavingsAccount.getSavingsAccountByIban(123456789, "BK01");
		savingsAccount.deposit(100);
		savingsAccount = SavingsAccount.getSavingsAccountByIban(123456788, "BK01");
		savingsAccount.deposit(32);
	}
	
	@Atomic(mode = TxMode.READ)
	public void atomicSavingsAccountDepositAssert() {
		SavingsAccount savingsAccount = SavingsAccount.getSavingsAccountByIban(123456789, "BK01");
		assertEquals(200, savingsAccount.getAmount());
		savingsAccount = SavingsAccount.getSavingsAccountByIban(123456788, "BK01");
		assertEquals(200, savingsAccount.getAmount());
	}
	
	@Test
	public void successSavingsAccountWithdraw() {
		atomicProcess();
		atomicSavingsAccountProcess();
		atomicSavingsAccountWithdrawProcess();
		atomicSavingsAccountWithdrawAssert();
	}
	
	@Atomic(mode = TxMode.WRITE)
	public void atomicSavingsAccountWithdrawProcess() {
		SavingsAccount savingsAccount = SavingsAccount.getSavingsAccountByIban(123456789, "BK01");
		savingsAccount.withdraw(99);
		savingsAccount = SavingsAccount.getSavingsAccountByIban(123456788, "BK01");
		savingsAccount.withdraw(200);
	}
	
	@Atomic(mode = TxMode.READ)
	public void atomicSavingsAccountWithdrawAssert() {
		SavingsAccount savingsAccount = SavingsAccount.getSavingsAccountByIban(123456789, "BK01");
		assertEquals(100, savingsAccount.getAmount());
		savingsAccount = SavingsAccount.getSavingsAccountByIban(123456788, "BK01");
		assertEquals(0, savingsAccount.getAmount());
	}

	@After
	@Atomic(mode = TxMode.WRITE)
	public void tearDown() {
		for (Bank bank : FenixFramework.getDomainRoot().getBankSet()) {
			for(Account account : bank.getAccountSet())
				account.delete();
			bank.delete();
		}
	}

}
