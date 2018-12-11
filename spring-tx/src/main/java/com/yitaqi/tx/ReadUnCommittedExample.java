package com.yitaqi.tx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * 
 * @ClassName: ReadUnCommittedExample
 * @Description: ������� ����--> ��ѯ--> �ع� 
 * @author xk
 * @date 2018��12��10��
  CREATE TABLE `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `accountName` varchar(255) COLLATE utf8_bin NOT NULL,
  `user` varchar(255) COLLATE utf8_bin NOT NULL,
  `money` int(11) NOT NULL,
  PRIMARY KEY (`id`)
	) ENGINE=InnoDB AUTO_INCREMENT=122 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
 */
public class ReadUnCommittedExample {
	
	public static Connection openConnection() throws ClassNotFoundException, SQLException {
//		Class.forName("com.mysql.jdbc.Driver");
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/txtest?useUnicode=true&useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC",
				"root", "123");
		return conn;
	}

	public static void main(String[] args) {
		
		// ���������߳�
		Thread t1 = run(new Runnable() {

			public void run() {
				try {
					insert("123", "tom", 1000);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		// ִ�в�ѯ�߳�
		run(new Runnable() {

			public void run() {
				try {
					Thread.sleep(500);
					Connection conn = openConnection();
					conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
					// ��������Ϊ�����ύ�����Խ�����
//					conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
					select("tom", conn);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		try {
			t1.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void insert(String accountName, String name, int money) throws SQLException, ClassNotFoundException {
		Connection conn = openConnection();
		conn.setAutoCommit(false);
		PreparedStatement prepareStatement = conn.prepareStatement("insert into account (accountName, user, money) values (?, ?, ?)");
		prepareStatement.setString(1, accountName);
		prepareStatement.setString(2, name);
		prepareStatement.setInt(3, money);
		prepareStatement.executeUpdate();
		System.out.println("ִ�в���");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conn.close();
	}
	
	public static void select(String name, Connection conn) throws SQLException, ClassNotFoundException {
		PreparedStatement prepareStatement = conn.prepareStatement("select * from account where user = ?");
		prepareStatement.setString(1, name);
		ResultSet resultSet = prepareStatement.executeQuery();
		System.out.println("ִ�в�ѯ");
		while (resultSet.next()) {
			for (int i = 1; i <= 4; i++) {
				System.out.print(resultSet.getString(i) + ", ");
			}
			System.out.println();
		}
	}
	
	public static Thread run(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.start();
		return thread;
	}
}
