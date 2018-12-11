package com.yitaqi.springtx;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yitaqi.service.AccountService;

public class AccountServiceImplTest {

	@Test
	public void test() {
		ClassPathXmlApplicationContext applicationContext 
			= new ClassPathXmlApplicationContext("spring-tx.xml");
		AccountService accountService = applicationContext.getBean(AccountService.class);
		accountService.addAccount("li", 100);
	}
}
