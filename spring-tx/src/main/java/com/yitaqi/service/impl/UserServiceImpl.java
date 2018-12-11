package com.yitaqi.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yitaqi.service.AccountService;
import com.yitaqi.service.UserService;

/**
 * 
 * @ClassName: UserServiceImpl
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author xk
 * @date 2018年12月11日
 CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `createTime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
 */

@Component
public class UserServiceImpl implements UserService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private AccountService accountService;

	@Transactional(propagation = Propagation.REQUIRED)
	public void createUser(String name) {
		jdbcTemplate.update("insert into user (name) values(?)", name);
		accountService.addAccount(name, 100);
		int i = 1 / 0;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//		addAccount(name, 100);
	// 获取当前代理对象   第二种 @AutoWired self  容易出错（循环依赖）
//		((UserService)AopContext.currentProxy()).addAccount(name, 100);
	/*@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void addAccount(String name, int money) {
		String accountName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		jdbcTemplate.update("insert into account (accountName, user, money) values (?, ?, ?)",
				accountName, name, money);
//		int i = 1 / 0;
	}*/

}
