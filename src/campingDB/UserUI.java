package campingDB;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.sql.Connection;

import java.sql.Date;

import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UserUI extends JFrame {
    public UserUI(int userId, Connection conn) throws SQLException {
        setTitle("사용자 기능 패널 - ID: " + userId);
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
            

        JTabbedPane tabs = new JTabbedPane();

        
     // -----------------------------------------------------------------------------------------

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

                    // car_id 조회
                    ResultSet carRs = s.executeQuery("SELECT car_id FROM camping_car WHERE car_name = '" + selectedCarName + "'");
                    if (carRs.next()) {
                        int carId = carRs.getInt("car_id");
                     

                        // rental 테이블에서 해당 car_id에 대한 모든 rental 기록 조회
                        String rentalQuery = "SELECT rental_start_day, rental_during FROM rental WHERE rental_car_id = " + carId;
                        ResultSet rentalRs = s.executeQuery(rentalQuery);

                        java.util.List<String> blockedPeriods = new java.util.ArrayList<>();

                        while (rentalRs.next()) {
                            java.sql.Date startDate = rentalRs.getDate("rental_start_day");
                            int during = rentalRs.getInt("rental_during");

                            // 대여 시작일부터 종료일까지
                            java.util.Calendar cal = java.util.Calendar.getInstance();

                            // start - 3일
                            cal.setTime(startDate);
                            cal.add(java.util.Calendar.DATE, -3);
                            java.sql.Date blockStart = new java.sql.Date(cal.getTimeInMillis());

                            // start + during - 1 + 3일
                            cal.setTime(startDate);
                            cal.add(java.util.Calendar.DATE, during - 1 + 3);
                            java.sql.Date blockEnd = new java.sql.Date(cal.getTimeInMillis());

                            blockedPeriods.add(blockStart.toString() + " ~ " + blockEnd.toString());
                        }

                        if (blockedPeriods.isEmpty()) {
                            availableDates.setText("현재 대여 불가 기간 없음");
                        } else {
                            availableDates.setText("대여 불가 기간:\n" + String.join("\n", blockedPeriods));
                        }

                        rentalRs.close();
                    } else {
                        availableDates.setText("캠핑카 정보를 찾을 수 없습니다.");
                    }
                    carRs.close();
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
            String durationStr = rentDurationField.getText().trim();
            
            if(durationStr.isEmpty()) {
               JOptionPane.showMessageDialog(null, "대여 기간을 입력하세요");
               return;}
     
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);


            java.sql.Date startDate;
            try {
               java.util.Date utilDate = sdf.parse(dateStr);
                startDate = new java.sql.Date(utilDate.getTime()); // 날짜 형식 검사
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(null, "올바른 날짜 형식으로 입력하세요.");
                return;
            }
//-----------------------------------------------------------------
            
            try {
                int duration = Integer.parseInt(durationStr);
                if (duration <= 0) throw new NumberFormatException();

                try (Statement s = conn.createStatement()) {
                    ResultSet rs = s.executeQuery("SELECT car_id, car_company_id FROM camping_car WHERE car_name = '" + selectedCarName + "'");
                    if (rs.next()) {
                        int carId = rs.getInt("car_id");
                        int companyId = rs.getInt("car_company_id");
                        
                        String o_Sql = "SELECT * FROM rental WHERE rental_car_id = " + carId +
                               " AND DATE('" + dateStr + "') BETWEEN DATE_SUB(rental_start_day, INTERVAL 3 DAY) " +
                               "AND DATE_ADD(rental_start_day, INTERVAL rental_during - 1 DAY)";
                        ResultSet overlap = s.executeQuery(o_Sql);
                        if (overlap.next()) {
                            JOptionPane.showMessageDialog(null, "해당 차량은 이미 선택한 날짜에 대여 중입니다.");
                            return;
                        }
                        overlap.close();  //겹치는 날짜 대여 불가 코드
                        
                        String dupSql = "SELECT * FROM rental WHERE rental_user_license = " + userId +
                                " AND rental_car_id = " + carId;
                        ResultSet dupRs = s.executeQuery(dupSql);
                        if (dupRs.next()) {
                            JOptionPane.showMessageDialog(null, "이미 이 차량을 대여하셨습니다.");
                            return;
                        }
                        dupRs.close();    //중복방지 코드
                        
                        
                        int totalPrice = 100000 * duration;// 대여 가격은 간단히 1일당 100000원으로 가정해서 계산
                        int a = -1;
                        try {
                           Statement idStmt = conn.createStatement();
                           ResultSet idRs = idStmt.executeQuery("SELECT rental_id FROM rental ORDER BY rental_id ASC");
                            java.util.List<Integer> idList = new java.util.ArrayList<>();
                               while (idRs.next()) {
                                   idList.add(idRs.getInt("rental_id"));
                               }
                               // 1~12 중 비어 있는 ID 찾기
                               for (int i = 1; i <= 12; i++) {
                                   if (!idList.contains(i)) {
                                       a = i;
                                       break;
                                   }
                               }
                               // 1~12 다 차 있으면 그 이후 최소값 찾기
                               if (a == -1) {
                                   int c = 13;
                                   while (idList.contains(c)) {
                                       c++;
                                   }
                                   a = c;
                               }
                               idRs.close();
                               idStmt.close();                                                
                        }
                        catch (SQLException ex) {
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "등록 실패11: " + ex.getMessage());}
                            //rental id 부여. 1부터 12까지 비어있으면 그 중 제일 앞을 부여하고 없으면 1씩 추가해 겹치지 않게 부여 


                        Calendar cal = Calendar.getInstance();
                        cal.setTime(startDate);
                        cal.add(Calendar.DATE, 7);
                        java.sql.Date paymentDueDate = new java.sql.Date(cal.getTimeInMillis());

                        
                 
                        String insertSql = String.format(
                               "INSERT INTO rental (rental_id, rental_car_id, rental_car_company_id, rental_user_license, rental_start_day, rental_during, rental_price, rental_payment_due_date, rental_etc_price, rental_etc_price_info ) " +
                               "VALUES (%d, %d, %d, %d, '%s', %d, %d, '%s', '%s', %s);",
                               a, carId, companyId, userId,
                               startDate.toString(), duration, totalPrice,
                               paymentDueDate.toString(), "0", "NULL"  // rental_etc_price_info는 null로
                           );
                        
                        s.executeUpdate(insertSql);
                        JOptionPane.showMessageDialog(null, "대여 등록 완료!");
                    }
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "대여 기간은 양의 정수로 입력하세요.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "등록 실패22: " + ex.getMessage());
            }
        });


        // 캠핑카 목록 불러오기 버튼
        JButton loadCarBtn = new JButton("캠핑카 목록 조회");
        loadCarBtn.addActionListener(e -> {
            try (Statement s = conn.createStatement();
                 ResultSet rs = s.executeQuery("SELECT * FROM camping_car")) {
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();

                DefaultTableModel model1 = new DefaultTableModel();
                for (int i = 1; i <= colCount; i++) {
                    model1.addColumn(meta.getColumnName(i));
                }

                while (rs.next()) {
                    Object[] row = new Object[colCount];
                    for (int i = 0; i < colCount; i++) {
                        row[i] = rs.getObject(i + 1);
                    }
                    model1.addRow(row);
                }

                carTable.setModel(model1);
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
        JTextField newDateField = new JTextField("YYYY-MM-DD", 10);
        JTextField durationField = new JTextField("3", 5);
        JButton applyChangeBtn = new JButton("일정 변경");
        
        dateChangePanel.setBorder(BorderFactory.createTitledBorder("일정 변경"));
        dateChangePanel.add(new JLabel("새 일자:"));
        dateChangePanel.add(newDateField);
        dateChangePanel.add(new JLabel("기간(일):"));
        dateChangePanel.add(durationField);
        dateChangePanel.add(applyChangeBtn);
        
        JPanel changeCarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        changeCarPanel.setBorder(BorderFactory.createTitledBorder("캠핑카 변경"));
        
        //캠핑카를 바꿀때 사용할 캠핑카 목록 가져오기
        ArrayList<String> newCarList = new ArrayList<String>();
        Statement st1 = conn.createStatement();
        ResultSet rs1 = st1.executeQuery("SELECT car_id FROM Camping_car");
        
        ResultSetMetaData meta1 = rs1.getMetaData();
        int colCount1 = meta1.getColumnCount();
        
        DefaultTableModel model1 = new DefaultTableModel();
        for (int i = 1; i <= colCount1; i++) {
            model1.addColumn(meta1.getColumnName(i));
        }
        
        while (rs1.next()) {
            newCarList.add(rs1.getString(1));
        }
        
        ArrayList<String> oldCarList = new ArrayList<String>();
        
        JComboBox<String> newCarBox = new JComboBox<>(new Vector<>(newCarList));
        JTextField changeDateField = new JTextField("YYYY-MM-DD", 10);
        JTextField changeDurationField = new JTextField("3", 5);
        JButton changeBtn = new JButton("변경 요청");

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

            String newStartDateStr = newDateField.getText().trim();
            String newDurationStr = durationField.getText().trim();

            if (newStartDateStr.isEmpty() || newDurationStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "새로운 날짜와 기간을 입력하세요.");
                return;
            }

            try {
                int newDuration = Integer.parseInt(newDurationStr);
                if (newDuration <= 0) throw new NumberFormatException();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                java.util.Date utilDate = sdf.parse(newStartDateStr);
                java.sql.Date newStartDate = new java.sql.Date(utilDate.getTime());

                try (Statement s = conn.createStatement()) {
                    // rental_id로 car_id, user_license 조회
                    ResultSet rs = s.executeQuery("SELECT rental_car_id, rental_user_license, rental_price FROM rental WHERE rental_id = " + rentalIdObj);
                    if (rs.next()) {
                        int carId = rs.getInt("rental_car_id");
                        int userLicense = rs.getInt("rental_user_license");
                        int oldprice = rs.getInt("rental_price");

                        // 겹치는 기간 대여 여부 확인
                        String overlapSql = String.format(
                            "SELECT * FROM rental WHERE rental_id != %s AND rental_car_id = %d " +
                            "AND DATE('%s') BETWEEN DATE_SUB(rental_start_day, INTERVAL 3 DAY) " +
                            "AND DATE_ADD(rental_start_day, INTERVAL rental_during - 1 + 3 DAY)",
                            rentalIdObj, carId, newStartDateStr
                        );
                        ResultSet overlap = s.executeQuery(overlapSql);
                        if (overlap.next()) {
                            JOptionPane.showMessageDialog(null, "해당 날짜에는 이미 다른 대여가 존재합니다.");
                            return;
                        }
                        overlap.close();
                        // 설정값들 변경
                        int newprice = newDuration * 100000; 
                      //rental_payment_due_date 설정
                        Calendar cal_ = Calendar.getInstance();
                        cal_.setTime(newStartDate);
                        cal_.add(Calendar.DATE, 7);
                        java.sql.Date paymentDueDate_ = new java.sql.Date(cal_.getTimeInMillis());
                        
                        //청구 요금
                        
                        int etc_price = 0;            //0보다 클 경우에 청구 요금 청구
                        if((newprice - oldprice) > 0)
                        	etc_price = newprice - oldprice;
                        String etcPriceStr = String.valueOf(etc_price);

                        // 업데이트
                        try (PreparedStatement ps = conn.prepareStatement(
                            "UPDATE rental SET rental_start_day = ?, rental_during = ? , rental_price =?, rental_payment_due_date =? , rental_etc_price = ?, rental_etc_price_info = ? WHERE rental_id = ?")) {
                            ps.setDate(1, newStartDate);
                            ps.setInt(2, newDuration);
                            ps.setInt(3, newprice);
                            ps.setDate(4, paymentDueDate_);
                            ps.setString(5, etcPriceStr);
                            ps.setString(6, "Changing car");
                            ps.setObject(7, rentalIdObj);
                            
                            
                            int updated = ps.executeUpdate();
                            if (updated > 0) {
                                JOptionPane.showMessageDialog(null, "일정이 변경되었습니다.");
                                loadRentsBtn.doClick();
                            } else {
                                JOptionPane.showMessageDialog(null, "일정 변경 실패.");
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "선택한 대여 정보를 찾을 수 없습니다.");
                    }
                }

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "기간은 양의 정수로 입력하세요.");
            } catch (ParseException pe) {
                JOptionPane.showMessageDialog(null, "날짜 형식이 잘못되었습니다. (예: 2025-06-05)");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "일정 변경 중 오류 발생: " + ex.getMessage());
            }
        });

        // [변경 요청] 클릭 시 → 캠핑카를 변경하는 ui 추가
        changeBtn.addActionListener(ev -> {
            int selectedRow = rentInfoTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(null, "변경할 대여 정보를 선택하세요.");
                return;
            }

            Object rentalIdObj = rentInfoTable.getValueAt(selectedRow, 0);
            if (rentalIdObj == null) {
                JOptionPane.showMessageDialog(null, "선택된 행의 rental_id가 없습니다.");
                return;
            }

            String newCarName = (String) newCarBox.getSelectedItem();
            if (newCarName == null) {
                JOptionPane.showMessageDialog(null, "변경할 차량을 선택하세요.");
                return;
            }
            int newCarName_id = Integer.parseInt(newCarName);

            try (Statement s = conn.createStatement()) {
                ResultSet carRs = s.executeQuery("SELECT car_id FROM camping_car WHERE car_id = '" + newCarName_id + "'");
                if (!carRs.next()) {
                    JOptionPane.showMessageDialog(null, "선택한 차량 정보를 찾을 수 없습니다.");
                    return;
                }

                int newCarId = carRs.getInt("car_id");

                // 기존 대여 정보 조회
                ResultSet rentRs = s.executeQuery("SELECT rental_start_day, rental_during, rental_user_license FROM rental WHERE rental_id = " + rentalIdObj);
                if (rentRs.next()) { 
                	int userLicense = rentRs.getInt("rental_user_license");
                	String startDateStr = changeDateField.getText();  // 사용자가 입력한 날짜 문자열
                	String startDuringStr = changeDurationField.getText(); // 사용자가 입력할 대여 기간 문자열
                	int duration;

                	try {
                	    duration = Integer.parseInt(startDuringStr);
                	    if (duration <= 0) {
                	        JOptionPane.showMessageDialog(null, "대여 기간은 1일 이상이어야 합니다.");
                	        return;
                	    }
                	} catch (NumberFormatException e) {
                	    JOptionPane.showMessageDialog(null, "대여 기간은 숫자로 입력해야 합니다.");
                	    return;
                	}

                	// 날짜 형식 검증
                	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                	sdf.setLenient(false);
                	java.util.Date parsedStartDate;

                	try {
                	    parsedStartDate = sdf.parse(startDateStr);  // 파싱 성공 시 정상
                	} catch (ParseException e) {
                	    JOptionPane.showMessageDialog(null, "날짜 형식이 잘못되었습니다. (예: 2025-06-05)");
                	    return;
                	}
                	java.sql.Date startDate3 = new java.sql.Date(parsedStartDate.getTime());
                    String endDateSql = String.format("DATE_ADD('%s', INTERVAL %d DAY)", startDate3, duration - 1);


                 // 겹치는 일정 확인 (start ~ end 전체 겹침 확인)
                    String overlapSql = String.format(
                        "SELECT * FROM rental " +
                        "WHERE rental_id != %s AND rental_car_id = %d " +
                        "AND (" +
                        "  (rental_start_day BETWEEN DATE_SUB('%s', INTERVAL 3 DAY) AND DATE_ADD(%s, INTERVAL 3 DAY)) " +
                        "  OR (DATE_ADD(rental_start_day, INTERVAL rental_during - 1 DAY) BETWEEN DATE_SUB('%s', INTERVAL 3 DAY) AND DATE_ADD(%s, INTERVAL 3 DAY)) " +
                        "  OR ('%s' BETWEEN rental_start_day AND DATE_ADD(rental_start_day, INTERVAL rental_during - 1 DAY))" +
                        ")",
                        rentalIdObj, newCarId,
                        startDate3, endDateSql,
                        startDate3, endDateSql,
                        startDate3 // 시작일이 기존 대여의 중간일 수 있음
                    );
                    ResultSet overlapRs = s.executeQuery(overlapSql);
                    if (overlapRs.next()) {
                        JOptionPane.showMessageDialog(null, "해당 차량은 선택한 기간에 이미 대여되어 있습니다.");
                        return;
                    }
                    overlapRs.close();

                    // 동일 차량 중복 대여 확인
                    String dupSql = String.format(
                        "SELECT * FROM rental WHERE rental_id != %s AND rental_car_id = %d AND rental_user_license = %d",
                        rentalIdObj, newCarId, userLicense
                    );
                    ResultSet dupRs = s.executeQuery(dupSql);
                    if (dupRs.next()) {
                        JOptionPane.showMessageDialog(null, "이미 해당 차량을 대여하신 기록이 있습니다.");
                        return;
                    }
                    dupRs.close();
                    
                    //------------------아래에서 이용할 변수들 설정하기
                    //a = rental_id 설정
                    
                    
                    int b = -1;
                    try {
                       Statement idStmt1 = conn.createStatement();
                       ResultSet idRs1 = idStmt1.executeQuery("SELECT rental_id FROM rental ORDER BY rental_id ASC");
                        java.util.List<Integer> idList1 = new java.util.ArrayList<>();
                           while (idRs1.next()) {
                               idList1.add(idRs1.getInt("rental_id"));
                           }
                           // 1~12 중 비어 있는 ID 찾기
                           for (int i = 1; i <= 12; i++) {
                               if (!idList1.contains(i)) {
                                   b = i;
                                   break;
                               }
                           }
                           // 1~12 다 차 있으면 그 이후 최소값 찾기
                           if (b == -1) {
                               int c1 = 13;
                               while (idList1.contains(c1)) {
                                   c1++;
                               }
                               b = c1;
                           }
                           idRs1.close();
                           idStmt1.close();                                                
                    }
                    catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "등록 실패11: " + ex.getMessage());}
                        //rental id 부여. 1부터 12까지 비어있으면 그 중 제일 앞을 부여하고 없으면 1씩 추가해 겹치지 않게 부여 


                    
                    //companyid 데이터베이스에서 꺼내기
                    int companyid = -1;

                    PreparedStatement getCompanyPs = conn.prepareStatement(
                        "SELECT car_company_id FROM camping_car WHERE car_id = ?"
                    );
                    getCompanyPs.setInt(1, newCarId);
                    ResultSet companyRs = getCompanyPs.executeQuery();

                    if (companyRs.next()) {
                        companyid = companyRs.getInt("car_company_id");
                    } else {
                        JOptionPane.showMessageDialog(null, "선택한 차량의 회사 정보를 찾을 수 없습니다.");
                        return;
                    }
                    companyRs.close();
                    getCompanyPs.close();
                    //rentalprice 설정
                    
                    int totalPrice2 = 100000 * duration;// 대여 가격은 간단히 1일당 100000원으로 가정해서 계산
                    
                    //rental_payment_due_date 설정
                    Calendar cal2 = Calendar.getInstance();
                    cal2.setTime(startDate3);
                    cal2.add(Calendar.DATE, 7);
                    java.sql.Date paymentDueDate1 = new java.sql.Date(cal2.getTimeInMillis());

                    // 캠핑카 변경
                    PreparedStatement ps = conn.prepareStatement(
                    	    "UPDATE rental SET rental_id = ?, rental_car_id = ?, rental_car_company_id = ?, rental_start_day = ?, rental_during = ? , rental_price = ?, rental_payment_due_date = ?, rental_etc_price = ?, rental_etc_price_info = ? WHERE rental_id = ?"
                    	);
                        ps.setInt(1, b);
                    	ps.setInt(2, newCarId);
                    	ps.setInt(3, companyid);
                    	ps.setDate(4, startDate3);
                    	ps.setInt(5, duration);
                    	ps.setInt(6, totalPrice2);
                    	ps.setDate(7, paymentDueDate1);
                    	ps.setString(8, "0");
                    	ps.setNull(9, java.sql.Types.VARCHAR);  //NULL로 설정
                    	ps.setObject(10, rentalIdObj);

                    int updated = ps.executeUpdate();
                    if (updated > 0) {
                        JOptionPane.showMessageDialog(null, "차량 변경이 완료되었습니다.");
                        loadRentsBtn.doClick();
                    } else {
                        JOptionPane.showMessageDialog(null, "차량 변경 실패.");
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "기존 대여 정보를 찾을 수 없습니다.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "차량 변경 중 오류 발생: " + ex.getMessage());
            }
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
