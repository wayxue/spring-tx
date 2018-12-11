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
 * @Description: 动态代理实现aop 事物管理
 * @author xk
 * @date 2018年12月11日
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
							System.out.println("开启事物： " + method.getName());
							return method.invoke(accountService, args);
						} finally {
							System.out.println("关闭事物：" + method.getName());
						}
					}
				});
		proxyAccountService.addAccount("leiao", 123);
	}
}
