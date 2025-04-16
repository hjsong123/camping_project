package campingDB;
import java.io.*;
import java.sql.*;

public class CampMain {
	public static void main (String[] args) {
		Connection conn;
		Statement stmt = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dev_campDB", "root",""); // JDBC 연결
			System.out.println("DB 연결 완료");
			
			stmt = conn.createStatement(); // SQL문 처리용 Statement 객체 생성
//			stmt.executeUpdate("INSERT into User (user_id, user_name, user_age) values (2, '이윤담', 23);"); // 레코드 추가
			printTable(stmt);

//			printTable(stmt);
//			
//			stmt.executeUpdate("update 고객 set 고객이름='홍길동' where 고객아이디='mango'"); //데이터 수정
//			printTable(stmt);
//			
//			stmt.executeUpdate("delete from 고객 where 고객이름='홍길동'"); // 레코드 삭제
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
		ResultSet srs = stmt.executeQuery("select * from User");
		while (srs.next()) {
			System.out.print(srs.getString("user_id"));
			System.out.print("\t|\t" + srs.getString("user_name"));
//			System.out.println("\t|\t" + srs.getString("user_age"));
		}
		System.out.println("=========================================");
	}

}
