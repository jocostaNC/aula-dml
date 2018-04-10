package pt.ulisboa.tecnico.softeng.bank.domain;

public class Account extends Account_Base {
	
	public Account(int iban, int amount, Bank bank) {
		setIban(iban);
		setAmount(amount);
		setBank(bank);

		bank.addAccount(this);
	}
	
	public Account() {
		super();
	}
	
	public void delete() {
		setBank(null);
		
		deleteDomainObject();
	}
	
	public static Account getAccountByIban(int iban, String code) {
		for (Account account : Bank.getBankByCode(code).getAccountSet()) {
			if (account.getIban() == iban) {
				return account;
			}
		}
		return null;
	}
	
	public void deposit(int amount) {
		if(amount > 0) {
			int newamount = this.getAmount() + amount;
			setAmount(newamount);
		}
	}
	
	public boolean withdraw(int amount) {
		if(this.getAmount() - amount >= 0) {
			this.setAmount(this.getAmount() - amount);
			return true;
		}
		return false;
	}
	
}