// login.java
package campingDB;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame {
    public Login(String userType) {
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
                String id = idField.getText();
                String pw = new String(pwField.getPassword());

                // 간단한 출력 (이 부분은 실제 로그인 처리로 교체 가능)
                new UserUI(1);
                dispose();
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
}
