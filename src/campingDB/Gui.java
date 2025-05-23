package campingDB;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;


public class Gui {

	public static void main(String[] args) {
        ShowLoginGUI();                          //GUI 로그인 시작 화면 호출
    }

    private static void ShowLoginGUI() {
        JFrame frame = new JFrame("캠핑카 대여 시스템 로그인");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);                                  //화면 기본 사이즈 
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 10, 10));

        JLabel title = new JLabel("로그인 방법을 선택하세요", SwingConstants.CENTER);
        title.setFont(new Font("맑은 고딕", Font.BOLD, 16));                //타이틀 글자 조정

        JButton adminBtn = new JButton("관리자 로그인");
        JButton userBtn = new JButton("회원 로그인");
        
        
        adminBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "관리자 로그인 성공!\n아이디: root\n비밀번호: 1234");
            frame.dispose();
            
        });

        userBtn.addActionListener(e -> {
            frame.dispose();
            
        });

        panel.add(new JLabel());  // 빈칸 삽입
        panel.add(title);
        panel.add(adminBtn);
        panel.add(userBtn);
        panel.add(new JLabel());  // 빈칸 삽입

        frame.add(panel);
        frame.setVisible(true);
    }
    


}







