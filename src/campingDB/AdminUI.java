package campingDB;
import javax.swing.*;
import java.awt.*;

public class AdminUI extends JFrame {
    public AdminUI() {
        setTitle("관리자 기능 패널");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 탭 인터페이스
        JTabbedPane tabs = new JTabbedPane();

        // 1. 데이터베이스 초기화
        JPanel initPanel = new JPanel();
        initPanel.setLayout(new FlowLayout());
        JButton initDBButton = new JButton("데이터베이스 초기화");
        initPanel.add(initDBButton);
        tabs.addTab("DB 초기화", initPanel);
        

        // 2. 데이터 입력 / 삭제 / 변경
        JPanel crudPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        crudPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        crudPanel.add(new JLabel("테이블명:"));
        crudPanel.add(new JTextField());
        crudPanel.add(new JLabel("조건식 입력 (WHERE ...):"));
        crudPanel.add(new JTextField());
        crudPanel.add(new JButton("입력"));
        crudPanel.add(new JButton("삭제"));
        crudPanel.add(new JButton("변경"));
        tabs.addTab("입력/삭제/변경", crudPanel);

        // 3. 전체 테이블 보기
        JPanel viewAllPanel = new JPanel(new BorderLayout());
        JButton viewAllBtn = new JButton("전체 테이블 보기");
        JTable dummyTable = new JTable(10, 5); // 더미 테이블
        viewAllPanel.add(viewAllBtn, BorderLayout.NORTH);
        viewAllPanel.add(new JScrollPane(dummyTable), BorderLayout.CENTER);
        tabs.addTab("전체 테이블 보기", viewAllPanel);

        // 4. 캠핑카 및 정비 내역 보기
        JPanel vehiclePanel = new JPanel(new BorderLayout());
        JComboBox<String> vehicleBox = new JComboBox<>(new String[]{"캠핑카1", "캠핑카2"});
        JTextArea infoArea = new JTextArea(10, 40);
        vehiclePanel.add(vehicleBox, BorderLayout.NORTH);
        vehiclePanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        tabs.addTab("정비 내역 조회", vehiclePanel);

        // 5. 임의 SQL 질의 실행
        JPanel sqlPanel = new JPanel(new BorderLayout());
        JTextArea sqlInput = new JTextArea("SELECT ...", 5, 60);
        JButton runSQL = new JButton("SQL 실행");
        JTable sqlResult = new JTable(10, 5);
        sqlPanel.add(new JScrollPane(sqlInput), BorderLayout.NORTH);
        sqlPanel.add(runSQL, BorderLayout.CENTER);
        sqlPanel.add(new JScrollPane(sqlResult), BorderLayout.SOUTH);
        tabs.addTab("SQL 실행", sqlPanel);

        add(tabs);
        setVisible(true);
    }

    public static void main(String[] args) {
        new AdminUI();
    }
}
