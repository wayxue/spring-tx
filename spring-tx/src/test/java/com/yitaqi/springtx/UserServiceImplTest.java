package com.yitaqi.springtx;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yitaqi.service.UserService;

public class UserServiceImplTest {

	// 测试场景
	// 1. createUser  无事物        addAccount(exception)  required  y n
	// 2. createUser  required    addAccount(exception)  无事物    n n
	// 3. createUser  required    addAccount(exception)  not_support    n y
	// 4. createUser  required    addAccount(exception)  required_new    n n
	// 5. createUser(exception)  required    addAccount  required_new    n y
	// 6. createUser(exception)  required    addAccount(move to the same class)  required_new    n y
	@Test
	public void test() {
		ClassPathXmlApplicationContext applicationContext = 
				new ClassPathXmlApplicationContext("spring-tx.xml");
		UserService userService = applicationContext.getBean(UserService.class);
		userService.createUser("lincoln");
	}
}
