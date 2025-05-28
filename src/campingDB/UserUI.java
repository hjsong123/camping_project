package campingDB;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
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

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField rentDateField = new JTextField("YYYY-MM-DD");
        JButton rentBtn = new JButton("대여 등록");
        formPanel.setBorder(BorderFactory.createTitledBorder("대여 신청"));
        formPanel.add(new JLabel("대여일자:"));
        formPanel.add(rentDateField);
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

            try (Statement s = conn.createStatement()) {
                ResultSet rs = s.executeQuery("SELECT car_id, car_company_id FROM camping_car WHERE car_name = '" + selectedCarName + "'");
                if (rs.next()) {
                    int carId = rs.getInt("car_id");
                    int companyId = rs.getInt("car_company_id");

                    String insertSql = String.format(
                        "INSERT INTO rental (rental_id, rental_car_id, rental_car_company_id, rental_user_license, rental_start_day, rental_during, rental_price, rental_payment_due_date) " +
                        "VALUES (NULL, %d, %d, %d, '%s', 3, 100000, '%s')",
                        carId, companyId, userId, dateStr, dateStr
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
