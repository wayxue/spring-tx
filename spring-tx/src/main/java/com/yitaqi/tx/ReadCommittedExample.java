package com.yitaqi.tx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: ReadCommittedExample
 * @Description: 不可重复读问题 查询--> 插入--> 查询
 * @author xk
 * @date 2018年12月11日
 *
 */
public class ReadCommittedExample {

	private static Object lock = new Object();
	
	public static Connection openConnection() throws ClassNotFoundException, SQLException {
//		Class.forName("com.mysql.jdbc.Driver");
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/txtest?useUnicode=true&useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC",
				"root", "123");
		return conn;
	}
	
	public static void main(String[] args) {
		// 开启插入线程
		Thread t1 = run(new Runnable() {

			public void run() {
				try {
					synchronized (lock) {
						lock.wait();
					}
					insert("123", "jack", 100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		// 开启查询线程
		Thread t2 = run(new Runnable() {

			public void run() {
				try {
					Connection conn = openConnection();
					conn.setAutoCommit(false);
					conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
					select("jack", conn);
					synchronized (lock) {
						lock.notify();
					}
					Thread.sleep(500);
					select("jack", conn);
					conn.close();
				} catch (ClassNotFoundException e) {
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
			t2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void insert(String accountName, String name, int money) throws SQLException, ClassNotFoundException {
		Connection conn = openConnection();
		PreparedStatement prepareStatement = conn.prepareStatement("insert into account (accountName, user, money) values (?, ?, ?)");
		prepareStatement.setString(1, accountName);
		prepareStatement.setString(2, name);
		prepareStatement.setInt(3, money);
		prepareStatement.executeUpdate();
		System.out.println("执行插入成功");
		conn.close();
	}
	
	public static void select(String name, Connection conn) throws SQLException, ClassNotFoundException {
		PreparedStatement prepareStatement = conn.prepareStatement("select * from account where user = ?");
		prepareStatement.setString(1, name);
		ResultSet resultSet = prepareStatement.executeQuery();
		System.out.println("执行查询");
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
