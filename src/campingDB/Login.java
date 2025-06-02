// login.java
package campingDB;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Login extends JFrame {
    public Login(String userType, Connection conn) {
        setTitle(userType.equals("admin") ? "관리자 로그인" : "사용자 로그인");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        JLabel idLabel = new JLabel("아이디:");
        JTextField idField = new JTextField();
        JLabel pwLabel = new JLabel("비밀번호:");
        JPasswordField pwField = new JPasswordField();
        JButton loginButton = new JButton("로그인");

        loginButton.addActionListener(new ActionListener() {
        	
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText().trim();
                String pw = new String(pwField.getPassword());

                // 간단한 출력 (이 부분은 실제 로그인 처리로 교체 가능)
                Statement stmt = null;
                try {
					stmt = conn.createStatement();
					printTable(stmt);
					
					String sql = "SELECT user_id FROM user WHERE user_account = '" + id +
	                         "' AND user_password = '" + pw + "'";
	            ResultSet rs = stmt.executeQuery(sql);

	            if (rs.next()) {
	                int userId = rs.getInt("user_id");
	                JOptionPane.showMessageDialog(null, "로그인 성공");
	                new UserUI(userId, conn); // 성공 시 UserUI로 이동
	                dispose(); // 로그인 창 닫기
	            } else {
	                JOptionPane.showMessageDialog(null, "아이디 또는 비밀번호가 틀렸습니다.");
	            }

	            rs.close();
	            stmt.close();

	        } catch (SQLException e1) {
	            e1.printStackTrace();
	        }
               
                
                
            }
        });

        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(idLabel);
        panel.add(idField);
        panel.add(pwLabel);
        panel.add(pwField);
        panel.add(new JLabel()); // 빈 공간
        panel.add(loginButton);

        add(panel);
        setVisible(true);
    }
    
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

