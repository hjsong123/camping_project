package campingDB;

import javax.swing.*;
import java.awt.*;

public class UserUI extends JFrame {
    public UserUI(int userId) {
        setTitle("사용자 기능 패널 - ID: " + userId);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // 1. 캠핑카 조회
        JPanel viewCarsPanel = new JPanel(new BorderLayout());
        JButton loadCarsBtn = new JButton("캠핑카 목록 조회");
        JTable carsTable = new JTable(10, 5); // 더미 테이블
        viewCarsPanel.add(loadCarsBtn, BorderLayout.NORTH);
        viewCarsPanel.add(new JScrollPane(carsTable), BorderLayout.CENTER);
        tabs.addTab("캠핑카 조회", viewCarsPanel);

        // 2. 대여 가능 일자 보기
        JPanel availableDatesPanel = new JPanel(new BorderLayout());
        JComboBox<String> carSelectBox = new JComboBox<>(new String[]{"캠핑카1", "캠핑카2"});
        JButton showDatesBtn = new JButton("대여 가능 일자 보기");
        JTextArea datesArea = new JTextArea(10, 40);
        availableDatesPanel.add(carSelectBox, BorderLayout.NORTH);
        availableDatesPanel.add(showDatesBtn, BorderLayout.CENTER);
        availableDatesPanel.add(new JScrollPane(datesArea), BorderLayout.SOUTH);
        tabs.addTab("대여 가능 일자", availableDatesPanel);

        // 3. 대여 등록
        JPanel rentPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        rentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rentPanel.add(new JLabel("캠핑카 선택:"));
        rentPanel.add(new JComboBox<>(new String[]{"캠핑카1", "캠핑카2"}));
        rentPanel.add(new JLabel("대여일자:"));
        rentPanel.add(new JTextField("YYYY-MM-DD"));
        rentPanel.add(new JLabel());
        rentPanel.add(new JButton("대여 등록"));
        tabs.addTab("대여 등록", rentPanel);

        // 4. 내 대여 정보 보기
        JPanel myRentInfoPanel = new JPanel(new BorderLayout());
        JButton myRentsBtn = new JButton("내 대여 내역 조회");
        JTable myRentTable = new JTable(10, 5);
        myRentInfoPanel.add(myRentsBtn, BorderLayout.NORTH);
        myRentInfoPanel.add(new JScrollPane(myRentTable), BorderLayout.CENTER);
        tabs.addTab("내 대여 정보", myRentInfoPanel);

        // 5. 대여 정보 삭제
        JPanel deleteRentPanel = new JPanel(new BorderLayout());
        JButton deleteRentBtn = new JButton("선택한 대여 정보 삭제");
        JTable deleteRentTable = new JTable(10, 5);
        deleteRentPanel.add(deleteRentBtn, BorderLayout.NORTH);
        deleteRentPanel.add(new JScrollPane(deleteRentTable), BorderLayout.CENTER);
        tabs.addTab("대여 정보 삭제", deleteRentPanel);

        // 6. 캠핑카 변경
        JPanel changeCarPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        changeCarPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        changeCarPanel.add(new JLabel("기존 대여 캠핑카:"));
        changeCarPanel.add(new JComboBox<>(new String[]{"캠핑카1", "캠핑카2"}));
        changeCarPanel.add(new JLabel("변경할 캠핑카:"));
        changeCarPanel.add(new JComboBox<>(new String[]{"캠핑카3", "캠핑카4"}));
        changeCarPanel.add(new JLabel());
        changeCarPanel.add(new JButton("변경 요청"));
        tabs.addTab("캠핑카 변경", changeCarPanel);

        // 7. 일정 변경
        JPanel changeDatePanel = new JPanel(new GridLayout(3, 2, 10, 10));
        changeDatePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        changeDatePanel.add(new JLabel("기존 대여일자:"));
        changeDatePanel.add(new JTextField("YYYY-MM-DD"));
        changeDatePanel.add(new JLabel("새로운 대여일자:"));
        changeDatePanel.add(new JTextField("YYYY-MM-DD"));
        changeDatePanel.add(new JLabel());
        changeDatePanel.add(new JButton("일정 변경"));
        tabs.addTab("대여 일정 변경", changeDatePanel);

        // 8. 외부 정비소 의뢰
        JPanel maintenancePanel = new JPanel(new GridLayout(3, 2, 10, 10));
        maintenancePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        maintenancePanel.add(new JLabel("정비 요청 캠핑카:"));
        maintenancePanel.add(new JComboBox<>(new String[]{"캠핑카1", "캠핑카2"}));
        maintenancePanel.add(new JLabel("정비소 선택:"));
        maintenancePanel.add(new JComboBox<>(new String[]{"정비소1", "정비소2"}));
        maintenancePanel.add(new JLabel());
        maintenancePanel.add(new JButton("정비 의뢰"));
        tabs.addTab("정비 의뢰", maintenancePanel);

        add(tabs);
        setVisible(true);
    }
}
