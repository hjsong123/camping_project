package campingDB;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
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

     // 1-2-3 통합: 캠핑카 조회 + 대여 가능일 확인 + 등록
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
        JButton loadCarBtn = new JButton("캠핑카 목록 조회");
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
        
        JPanel btmPanel = new JPanel();
        btmPanel.setLayout(new BoxLayout(btmPanel, BoxLayout.Y_AXIS));
        
        JPanel dateChangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField oldDateField = new JTextField("YYYY-MM-DD", 10);
        JTextField newDateField = new JTextField("YYYY-MM-DD", 10);
        JTextField durationField = new JTextField("3", 5);
        JButton applyChangeBtn = new JButton("일정 변경");
        
        dateChangePanel.setBorder(BorderFactory.createTitledBorder("일정 변경"));
        dateChangePanel.add(new JLabel("기존 일자:"));
        dateChangePanel.add(oldDateField);
        dateChangePanel.add(new JLabel("새 일자:"));
        dateChangePanel.add(newDateField);
        dateChangePanel.add(new JLabel("기간(일):"));
        dateChangePanel.add(durationField);
        dateChangePanel.add(applyChangeBtn);
        
        JPanel changeCarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        changeCarPanel.setBorder(BorderFactory.createTitledBorder("캠핑카 변경"));

        JComboBox<String> oldCarBox = new JComboBox<>(new Vector<>());
        JComboBox<String> newCarBox = new JComboBox<>(new Vector<>());
        JTextField changeDateField = new JTextField("YYYY-MM-DD", 10);
        JTextField changeDurationField = new JTextField("3", 5);
        JButton changeBtn = new JButton("변경 요청");

        changeCarPanel.add(new JLabel("기존 캠핑카:"));
        changeCarPanel.add(oldCarBox);
        changeCarPanel.add(new JLabel("새 캠핑카:"));
        changeCarPanel.add(newCarBox);
        changeCarPanel.add(new JLabel("시작일:"));
        changeCarPanel.add(changeDateField);
        changeCarPanel.add(new JLabel("기간(일):"));
        changeCarPanel.add(changeDurationField);
        changeCarPanel.add(changeBtn);
        
        btmPanel.add(dateChangePanel);
        btmPanel.add(changeCarPanel);
        
        rentInfoPanel.add(topBtns, BorderLayout.NORTH);
        rentInfoPanel.add(rentScrollPane, BorderLayout.CENTER);
        rentInfoPanel.add(btmPanel, BorderLayout.SOUTH);
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
        
     // [일정변경 버튼] 클릭 시 → 일정을 변경하는 ui 추가
        applyChangeBtn.addActionListener(e -> {
            int selectedRow = rentInfoTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "일정 변경할 대여 정보를 선택하세요.");
                return;
            }

            Object rentalIdObj = rentInfoTable.getValueAt(selectedRow, 0);
            if (rentalIdObj == null) {
                JOptionPane.showMessageDialog(null, "선택된 행의 rental_id가 없습니다.");
                return;
            }

            String newStartDate = newDateField.getText().trim();
            String durationText = durationField.getText().trim();

            if (newStartDate.isEmpty() || durationText.isEmpty()) {
                JOptionPane.showMessageDialog(null, "새로운 날짜와 기간을 입력하세요.");
                return;
            }

            try {
                int newDuration = Integer.parseInt(durationText);

                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE rental SET rental_start_day = ?, rental_during = ? WHERE rental_id = ?"
                )) {
                    ps.setString(1, newStartDate);
                    ps.setInt(2, newDuration);
                    ps.setObject(3, rentalIdObj);

                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        JOptionPane.showMessageDialog(null, "일정이 변경되었습니다.");
                        loadRentsBtn.doClick(); // 변경된 테이블 새로고침
                    } else {
                        JOptionPane.showMessageDialog(null, "일정 변경 실패.");
                    }
                }

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "기간은 숫자로 입력하세요.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "일정 변경 중 오류 발생: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // [변경 요청] 클릭 시 → 캠핑카를 변경하는 ui 추가
        changeBtn.addActionListener(ev -> {
        	
        });

        // 8. 외부 정비소 의뢰
        ArrayList<String> arr1 = new ArrayList<String>();
        ArrayList<String> arr2 = new ArrayList<String>();
        
        // 유저가 예약한 캠핑카에 대해서 선택하기.
        try (
        		Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(
                		"SELECT DISTINCT camping_car.car_name " +
                		"FROM camping_car JOIN rental ON camping_car.car_id = rental.rental_car_id " +
                		"WHERE rental.rental_user_license = " + userId
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
                       arr1.add(rs.getString("car_name"));
                   }
               }

           } catch (SQLException ex) {
               ex.printStackTrace();
               JOptionPane.showMessageDialog(null, "조회 실패: " + ex.getMessage());
           }
        
        // 정비소 전체 목록 보여주기
        try (
        		Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(
                		"SELECT repair_shop_name FROM car_repair_shop"
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
                       arr2.add(rs.getString(1));
                   }
               }

           } catch (SQLException ex) {
               ex.printStackTrace();
               JOptionPane.showMessageDialog(null, "조회 실패: " + ex.getMessage());
           }
        
        JComboBox<String> car_box = new JComboBox<>(new Vector<String>(arr1));
        JComboBox<String> repair_box = new JComboBox<>(new Vector<String>(arr2));

        JTextArea contentArea = new JTextArea(5, 40);
        contentArea.setLineWrap(true);
        contentArea.setBorder(BorderFactory.createTitledBorder("정비 요청 내용 입력"));

        JButton request = new JButton("정비 의뢰");
        JPanel repair_panel = new JPanel(new GridLayout(3, 2, 10, 10));
        repair_panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        repair_panel.add(new JLabel("정비 요청 캠핑카:"));
        repair_panel.add(car_box);
        repair_panel.add(new JLabel("정비소 선택:"));
        repair_panel.add(repair_box);
        repair_panel.add(new JLabel());
        repair_panel.add(request);

        JPanel maintenancePanel = new JPanel(new BorderLayout(10, 10));
        maintenancePanel.add(repair_panel, BorderLayout.NORTH);
        maintenancePanel.add(new JScrollPane(contentArea), BorderLayout.CENTER);
        
        //작성한 정보를 바탕으로 정비 의뢰하기
        request.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 의뢰를 작성할 필수 요건들 => 나머지는 null을 허용하여 나중에 수정 가능 하도록
				int repairCount = 0, carId = 0, carCompanyId = 0, carRepairShopId = 0;
				try (
						// 의뢰 주문서의 id를 동적으로 생성하기.
		        		Statement st = conn.createStatement();
		                ResultSet rs = st.executeQuery(
		                		"SELECT * FROM repair"
		                )) {

		               while (rs.next()) {
		                   repairCount++;
		               }
		               System.out.println(repairCount);
		               // 의뢰할 차량 번호와 차 회사 번호
		               ResultSet rs2 = st.executeQuery(
		                		"SELECT car_id, car_company_id FROM camping_car WHERE car_name = '" + (String)car_box.getSelectedItem() + "'"
		                );
		               
		               ResultSetMetaData meta2 = rs2.getMetaData();
		               int colCount = meta2.getColumnCount();
		               
		               while (rs2.next()) {
		                   Object[] row = new Object[colCount];
		                   for (int i = 0; i < colCount; i++) {
		                       carId = rs2.getInt(1);
		                       carCompanyId = rs2.getInt(2);
		                   }
		               }
		               
		               //의뢰할 외부 수리점 가게 번호
		               ResultSet rs3 = st.executeQuery(
		                		"SELECT repair_shop_id FROM car_repair_shop WHERE repair_shop_name = '" + (String)repair_box.getSelectedItem() + "'"
		                );
		               
		               ResultSetMetaData meta3 = rs3.getMetaData();
		               colCount = meta3.getColumnCount();
		               
		               while (rs3.next()) {
		                   Object[] row = new Object[colCount];
		                   for (int i = 0; i < colCount; i++) {
		                       carRepairShopId = rs3.getInt(1);
		                   }
		               }
		               
		               try {
		            	   st.executeUpdate("INSERT INTO repair (repair_id, repair_car_id, repair_repair_shop_id, repair_car_company_id, repair_user_license, repair_content)\r\n"
				               		+ "VALUES ("+ (repairCount + 1) +","+ carId +","+ carRepairShopId +","+ carCompanyId +","+ userId +",'"+ contentArea.getText() +"');\r\n");
		            	   JOptionPane.showMessageDialog(null, "성공적으로 저장되었습니다.");
		               }
		               catch(SQLException ex){
		            	   JOptionPane.showMessageDialog(null, "조회 실패: " + ex.getMessage());
		               }
		           } 
				catch (SQLException ex) {
		               ex.printStackTrace();
		               JOptionPane.showMessageDialog(null, "조회 실패: " + ex.getMessage());
		           }
			}
		});

        tabs.addTab("정비 의뢰", maintenancePanel);

        

        add(tabs);
        setVisible(true);
    }
}
