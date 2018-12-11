package com.yitaqi.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.yitaqi.service.AccountService;

@Component
public class AccountServiceImpl implements AccountService{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void addAccount(String name, int money) {
		String accountName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		jdbcTemplate.update("insert into account (accountName, user, money) values (?, ?, ?)",
				accountName, name, money);
//		int i = 1 / 0;
	}

	@Transactional
	public int updateAccount(String name, int money) {
		
		return jdbcTemplate.update("update account set money = ? where user = ?", money, name);
	}

}
