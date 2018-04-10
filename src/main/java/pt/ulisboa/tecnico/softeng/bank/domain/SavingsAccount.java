package pt.ulisboa.tecnico.softeng.bank.domain;

public class SavingsAccount extends SavingsAccount_Base {
    
	public SavingsAccount(int iban, int amount, Bank bank) {
		setIban(iban);
		setAmount(amount);
		setBank(bank);

		bank.addAccount(this);
	}
	
    public SavingsAccount() {
        super();
    }
    
    public static SavingsAccount getSavingsAccountByIban(int iban, String code) {
		for (Account account : Bank.getBankByCode(code).getAccountSet()) {
			if (account.getIban() == iban) {
				return (SavingsAccount) account;
			}
		}
		return null;
	}
    
    @Override
    public void deposit(int amount) {
		if(amount%100 == 0) {
			int newamount = this.getAmount() + amount;
			setAmount(newamount);
		}
	}
	
    @Override
	public boolean withdraw(int amount) {
		if(this.getAmount() == amount) {
			this.setAmount(this.getAmount() - amount);
			return true;
		}
		return false;
	}
}
