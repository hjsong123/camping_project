// choice.java
package campingDB;
import javax.swing.*;
import java.awt.event.*;

public class Choice extends JFrame {
    public Choice() {
        setTitle("유저 선택");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        JButton adminButton = new JButton("관리자");
        JButton userButton = new JButton("사용자");

        adminButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AdminUI();
                dispose(); // 현재 창 닫기
            }
        });

        userButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Login("user");
                dispose(); // 현재 창 닫기
            }
        });

        panel.add(adminButton);
        panel.add(userButton);
        add(panel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new Choice();
    }
}
