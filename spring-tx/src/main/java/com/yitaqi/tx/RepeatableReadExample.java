package com.yitaqi.tx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * @ClassName: RepeatableReadExample
 * @Description: ���ظ�����mysqlĬ�ϣ����۶�ȡ���ٴΣ����ǵ�һ�εĽ��
 * @author xk
 * @date 2018��12��11��
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
//		���������߳�
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
		// ������ѯ�߳�
		Thread t2 = run(new Runnable() {

			public void run() {
				try {
					Connection conn = openConnection();
					conn.setAutoCommit(false);
//					conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
					// ��һ�β�ѯ
					select("jack", conn);
					synchronized (lock) {
						lock.notify();
					}
					Thread.sleep(500);
					// �ڶ��β�ѯ
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
		System.out.println("ִ���޸ĳɹ�");
	}
	
	public static void select(String name, Connection conn) throws SQLException {
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
