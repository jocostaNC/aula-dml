package pt.ulisboa.tecnico.softeng.bank.domain;

import pt.ist.fenixframework.FenixFramework;

public class Bank extends Bank_Base {

	public Bank(String name, String code) {
		setName(name);
		setCode(code);

		FenixFramework.getDomainRoot().addBank(this);
	}

	public void delete() {
		setRoot(null);

		deleteDomainObject();
	}

	public static Bank getBankByCode(String code) {
		for (Bank bank : FenixFramework.getDomainRoot().getBankSet()) {
			if (bank.getCode().equals(code)) {
				return bank;
			}
		}
		return null;
	}
	
	public int totalBalance() {
		int total = 0;
		for(Account account : this.getAccountSet()) {
			total += account.getAmount();
		}
		
		return total;
	}

}
