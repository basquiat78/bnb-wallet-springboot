package io.basquiat.common.code;

public enum BeanNameCode {

	RECEIVESERVICE("receiveService"),
	
	WALLET("wallet");
	
	public String beanName;
	
	BeanNameCode(String beanName) {
		this.beanName = beanName;
	}
	
}
