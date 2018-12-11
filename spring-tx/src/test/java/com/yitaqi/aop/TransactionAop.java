package com.yitaqi.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.yitaqi.service.AccountService;

/**
 * 
 * @ClassName: TransactionAop
 * @Description: ��̬����ʵ��aop �������
 * @author xk
 * @date 2018��12��11��
 *
 */
public class TransactionAop {

	@Test
	public void test() {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-tx.xml");
		final AccountService accountService = applicationContext.getBean(AccountService.class);
		AccountService proxyAccountService = (AccountService) Proxy.newProxyInstance(AccountService.class.getClassLoader(), new Class[]{AccountService.class}, 
				new InvocationHandler() {
					
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						try {
							System.out.println("������� " + method.getName());
							return method.invoke(accountService, args);
						} finally {
							System.out.println("�ر����" + method.getName());
						}
					}
				});
		proxyAccountService.addAccount("leiao", 123);
	}
}
