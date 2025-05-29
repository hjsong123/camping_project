package campingDB;

import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class UserUI extends JFrame {
    public UserUI(int userId, Connection conn) {
        setTitle("사용자 기능 패널 - ID: " + userId);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        Statement stmt = null;

        JTabbedPane tabs = new JTabbedPane();

        // 1. 캠핑카 조회
        JPanel viewCarsPanel = new JPanel(new BorderLayout());
        JButton loadCarsBtn = new JButton("캠핑카 목록 조회");
        JTable carsTable = new JTable(10, 5); // 더미 테이블
        viewCarsPanel.add(loadCarsBtn, BorderLayout.NORTH);
        viewCarsPanel.add(new JScrollPane(carsTable), BorderLayout.CENTER);
        tabs.addTab("캠핑카 조회", viewCarsPanel);

     // 2-3 통합: 캠핑카 조회 + 대여 가능일 확인 + 등록
        JPanel rentalPanel = new JPanel(new BorderLayout(10, 10));
        JTable carTable = new JTable();
        DefaultTableModel carModel = new DefaultTableModel();
        carTable.setModel(carModel);

        JTextArea availableDates = new JTextArea(5, 40);
        availableDates.setEditable(false);
        availableDates.setBorder(BorderFactory.createTitledBorder("대여 가능 일자"));

     // ⬇️ 기존 대여 입력 폼 확장
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        JTextField rentDateField = new JTextField("YYYY-MM-DD");
        JTextField rentDurationField = new JTextField("3"); // 대여 기간 (일 수)
        JButton rentBtn = new JButton("대여 등록");
        formPanel.setBorder(BorderFactory.createTitledBorder("대여 신청"));

        formPanel.add(new JLabel("대여일자:"));
        formPanel.add(rentDateField);
        formPanel.add(new JLabel("대여 기간 (일):"));
        formPanel.add(rentDurationField);
        formPanel.add(new JLabel());
        formPanel.add(rentBtn);


        // 캠핑카 테이블 선택 시 → 대여 가능 일자 표시
        carTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && carTable.getSelectedRow() != -1) {
                String selectedCarName = (String) carTable.getValueAt(carTable.getSelectedRow(), 1); // car_name
                try (Statement s = conn.createStatement()) {
                    ResultSet rs = s.executeQuery(
                        "SELECT car_date FROM camping_car WHERE car_name = '" + selectedCarName + "'"
                    );
                    
                    ResultSet rs2 = s.executeQuery(
                            "SELECT rental_start_day, rental_during FROM rental WHERE rental_car_id = ("
                            + "SELECT car_id FROM Camping_car where car_name =" + selectedCarName + ")"
                        );
                    if (rs.next()) {
                        availableDates.setText("가능 시작일: " + rs.getDate("car_date").toString());
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    availableDates.setText("조회 실패");
                }
            }
        });

        // 대여 등록 버튼 클릭 시 → rental 테이블 삽입
        rentBtn.addActionListener(e -> {
            int selectedRow = carTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "캠핑카를 먼저 선택하세요.");
                return;
            }

            String selectedCarName = (String) carTable.getValueAt(selectedRow, 1);
            String dateStr = rentDateField.getText().trim();
            int rentalDays;

            try {
                rentalDays = Integer.parseInt(rentDurationField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "대여 기간은 숫자로 입력해주세요.");
                return;
            }

            try (Statement s = conn.createStatement()) {
                ResultSet rs = s.executeQuery("SELECT car_id, car_company_id FROM camping_car WHERE car_name = '" + selectedCarName + "'");
                if (rs.next()) {
                    int carId = rs.getInt("car_id");
                    int companyId = rs.getInt("car_company_id");

                    String insertSql = String.format(
                        "INSERT INTO rental (rental_id, rental_car_id, rental_car_company_id, rental_user_license, rental_start_day, rental_during, rental_price, rental_payment_due_date) " +
                        "VALUES (NULL, %d, %d, %d, '%s', %d, 100000, '%s')",
                        carId, companyId, userId, dateStr, rentalDays, dateStr
                    );
                    s.executeUpdate(insertSql);
                    JOptionPane.showMessageDialog(null, "대여 등록 완료!");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "등록 실패: " + ex.getMessage());
            }
        });


        // 캠핑카 목록 불러오기 버튼
        JButton loadCarBtn = new JButton("캠핑카 목록 불러오기");
        loadCarBtn.addActionListener(e -> {
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT * FROM camping_car")) {
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();

                DefaultTableModel model = new DefaultTableModel();
                for (int i = 1; i <= colCount; i++) {
                    model.addColumn(meta.getColumnName(i));
                }

                while (rs.next()) {
                    Object[] row = new Object[colCount];
                    for (int i = 0; i < colCount; i++) {
                        row[i] = rs.getObject(i + 1);
                    }
                    model.addRow(row);
                }

                carTable.setModel(model);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(loadCarBtn, BorderLayout.NORTH);
        topPanel.add(new JScrollPane(carTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(availableDates, BorderLayout.NORTH);
        bottomPanel.add(formPanel, BorderLayout.CENTER);

        rentalPanel.add(topPanel, BorderLayout.CENTER);
        rentalPanel.add(bottomPanel, BorderLayout.SOUTH);

        tabs.addTab("캠핑카 대여", rentalPanel);


     // 4-5 통합: 내 대여 정보 보기 + 선택 삭제
        JPanel rentInfoPanel = new JPanel(new BorderLayout());
        JButton loadRentsBtn = new JButton("내 대여 내역 불러오기");
        JButton deleteSelectedBtn = new JButton("선택한 대여 정보 삭제");

        JTable rentInfoTable = new JTable();
        JScrollPane rentScrollPane = new JScrollPane(rentInfoTable);

        // 상단 버튼 패널
        JPanel topBtns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBtns.add(loadRentsBtn);
        topBtns.add(deleteSelectedBtn);

        rentInfoPanel.add(topBtns, BorderLayout.NORTH);
        rentInfoPanel.add(rentScrollPane, BorderLayout.CENTER);
        tabs.addTab("내 대여 정보 관리", rentInfoPanel);

        // [불러오기 버튼] 클릭 시 → 내 대여 정보 조회
        loadRentsBtn.addActionListener(e -> {
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(
                     "SELECT * FROM rental WHERE rental_user_license = " +
                     "(SELECT user_id FROM user WHERE user_id = " + userId + ")"
                 )) {

                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();

                DefaultTableModel model = new DefaultTableModel();
                for (int i = 1; i <= colCount; i++) {
                    model.addColumn(meta.getColumnName(i));
                }

                while (rs.next()) {
                    Object[] row = new Object[colCount];
                    for (int i = 0; i < colCount; i++) {
                        row[i] = rs.getObject(i + 1);
                    }
                    model.addRow(row);
                }

                rentInfoTable.setModel(model);

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "조회 실패: " + ex.getMessage());
            }
        });

        // [삭제 버튼] 클릭 시 → 선택된 rental_id 삭제
        deleteSelectedBtn.addActionListener(e -> {
            int selectedRow = rentInfoTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "삭제할 대여 정보를 선택하세요.");
                return;
            }

            Object rentalIdObj = rentInfoTable.getValueAt(selectedRow, 0); // rental_id assumed to be column 0
            if (rentalIdObj == null) {
                JOptionPane.showMessageDialog(null, "선택된 행의 rental_id가 없습니다.");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(null,
                    "선택한 대여 정보(rental_id=" + rentalIdObj + ")를 삭제하시겠습니까?", "확인",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            try (Statement st = conn.createStatement()) {
                int result = st.executeUpdate("DELETE FROM rental WHERE rental_id = " + rentalIdObj);
                if (result > 0) {
                    JOptionPane.showMessageDialog(null, "삭제 완료!");
                    ((DefaultTableModel) rentInfoTable.getModel()).removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(null, "삭제할 수 없습니다.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "삭제 실패: " + ex.getMessage());
            }
        });


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
