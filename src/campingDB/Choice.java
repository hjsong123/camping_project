// choice.java
package campingDB;
import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Choice extends JFrame {
    public Choice(Connection conn) {
        setTitle("유저 선택");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        JButton adminButton = new JButton("관리자");
        JButton userButton = new JButton("사용자");

        adminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AdminUI(conn);
                dispose(); // 현재 창 닫기
            }
        });

        userButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Login("user", conn);
                dispose(); // 현재 창 닫기
            }
        });

        panel.add(adminButton);
        panel.add(userButton);
        add(panel);

        setVisible(true);
    }

    public static void main(String[] args) {
    	Connection conn = null;
		Statement stmt = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root",""); // JDBC 연결
			System.out.println("DB 연결 완료");
			

		} catch (ClassNotFoundException e) {
			System.out.println("JDBC 드라이버 로드 오류");
		} catch (SQLException e) {
			System.out.println("SQL 실행 오류");
			e.printStackTrace();
		}
		
        new Choice(conn);
    }
}
