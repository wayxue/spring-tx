package com.yitaqi.tx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: RepeatableReadExample
 * @Description: 可重复读，mysql默认，无论读取多少次，都是第一次的结果
 * @author xk
 * @date 2018年12月11日
 *
 */
public class RepeatableReadExample {

	private static Object lock = new Object();
	
	public static Connection openConnection() throws ClassNotFoundException, SQLException {
//		Class.forName("com.mysql.jdbc.Driver");
		Class.forName("com.mysql.cj.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/txtest?useUnicode=true&useSSL=false&characterEncoding=UTF-8&serverTimezone=UTC",
				"root", "123");
		return conn;
	}
	
	public static void main(String[] args) {
//		开启更新线程
		Thread t1 = run(new Runnable() {

			public void run() {
				synchronized (lock) {
					try {
						lock.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				try {
					update("jack");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (SQLException e) {
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
//					conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
					// 第一次查询
					select("jack", conn);
					synchronized (lock) {
						lock.notify();
					}
					Thread.sleep(500);
					// 第二次查询
					select("jack", conn);
					conn.close();
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
	
	public static void update(String name) throws ClassNotFoundException, SQLException {
		Connection conn = openConnection();
		PreparedStatement prepareStatement = conn.prepareStatement("update account set money = money + 1 where user = ?");
		prepareStatement.setString(1, name);
		prepareStatement.executeUpdate();
		conn.close();
		System.out.println("执行修改成功");
	}
	
	public static void select(String name, Connection conn) throws SQLException {
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
