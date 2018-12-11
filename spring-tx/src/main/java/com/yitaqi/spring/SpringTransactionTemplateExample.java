package com.yitaqi.spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @ClassName: SpringTransactionTemplateExample
 * @Description: spring ���ʽ����
 * @author xk
 * @date 2018��12��11��
 *
 */
public class SpringTransactionTemplateExample {

	
	private static String url = "jdbc:mysql://localhost:3306/txtest?useUnicode=true&useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC";
	private static String user = "root";
	private static String password = "123";
	
	public static void main(String[] args) {
		
		final DataSource dataSource = new DriverManagerDataSource(url, user, password);
		TransactionTemplate transactionTemplate = new TransactionTemplate();
		transactionTemplate.setTransactionManager(new DataSourceTransactionManager(dataSource));
		transactionTemplate.execute(new TransactionCallback<Object>() {

			public Object doInTransaction(TransactionStatus status) {
				Connection conn = DataSourceUtils.getConnection(dataSource);
				Object savePoint = null;
				try {
					// ����
					{
						PreparedStatement prepareStatement = conn.prepareStatement("insert into account (accountName, user, money) values (?, ?, ?)");
						prepareStatement.setString(1, "123");
						prepareStatement.setString(2, "tom");
						prepareStatement.setInt(3, 100);
						prepareStatement.executeUpdate();
					}
					
					// ���ñ����
//					savePoint = status.createSavepoint();
					
					// ����
					{
						PreparedStatement prepareStatement = conn.prepareStatement("insert into account (accountName, user, money) values (?, ?, ?)");
						prepareStatement.setString(1, "456");
						prepareStatement.setString(2, "jack");
						prepareStatement.setInt(3, 100);
						prepareStatement.executeUpdate();
					}
					
					// ����
					{
						PreparedStatement prepareStatement = conn.prepareStatement("update account set money = money + 1 where user = ?");
						prepareStatement.setString(1, "jerry");
						int i = 1 / 0;
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("����ʧ��");
					if (savePoint != null) {
						status.rollbackToSavepoint(savePoint);
					} else {
						status.setRollbackOnly();
					}
				}
				return null;
			}
			
		});
	}
}
