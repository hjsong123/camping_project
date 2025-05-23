package campingDB;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;


public class CampMain {
	public static void main (String[] args) {
		Connection conn;
		Statement stmt = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root",""); // JDBC 연결
			System.out.println("DB 연결 완료");
			

			stmt = conn.createStatement(); // SQL문 처리용 Statement 객체 생성(Test용)
//			stmt.executeUpdate("INSERT INTO user VALUES (1, 1001, 'user1', 'pass1', 'User1', 'UserAddr1', '010-1307-5883', 'user1@example.com', '2022-12-21', 'AB-1006')"); // 레코드 추가
//			stmt.executeUpdate("INSERT INTO user VALUES (2, 1002, 'user2', 'pass2', 'User2', 'UserAddr2', '010-1185-8570', 'user2@example.com', '2020-08-27', 'AB-1010')");

			printTable(stmt);
			
			
			//위의 삽입한 데이터 삭제하기.(Test용)
//			stmt.executeUpdate("DELETE FROM User WHERE User_id = 1");
//			printTable(stmt);

		} catch (ClassNotFoundException e) {
			System.out.println("JDBC 드라이버 로드 오류");
		} catch (SQLException e) {
			System.out.println("SQL 실행 오류");
			e.printStackTrace();
		}
		
	}
	// 레코드의 각 열의 값 화면에 출력
	private static void printTable(Statement stmt) throws SQLException {
		ResultSet srs = stmt.executeQuery("select * from user");
		while (srs.next()) {
			System.out.print(srs.getString("user_id"));
			System.out.print("\t|\t" + srs.getString("user_name"));
			System.out.println("");
		}
		System.out.println("=========================================");
	}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

