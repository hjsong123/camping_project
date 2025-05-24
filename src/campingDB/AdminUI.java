package campingDB;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminUI extends JFrame {
    public AdminUI(Connection conn) {
		
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
        
        initDBButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
        		Statement stmt = null;
        		
            	try {
					stmt = conn.createStatement();
					stmt.executeUpdate("delete from employee;");
					stmt.executeUpdate("delete from repair;");
					stmt.executeUpdate("delete from car_repair_shop;");
					stmt.executeUpdate("delete from rental;");
					stmt.executeUpdate("delete from car_fixlog;");
					stmt.executeUpdate("delete from car_item;");
					stmt.executeUpdate("delete from camping_car;");
					stmt.executeUpdate("delete from camping_company;");
					stmt.executeUpdate("delete from User;");
					
					stmt.executeUpdate("INSERT INTO camping_company VALUES (1, 'Company1', 'Address1', '010-6549-3690', 'Owner1', 'owner1@example.com'), (2, 'Company2', 'Address2', '010-2015-3208', 'Owner2', 'owner2@example.com'), (3, 'Company3', 'Address3', '010-2934-2966', 'Owner3', 'owner3@example.com'), (4, 'Company4', 'Address4', '010-7503-4243', 'Owner4', 'owner4@example.com'), (5, 'Company5', 'Address5', '010-9388-1276', 'Owner5', 'owner5@example.com'), (6, 'Company6', 'Address6', '010-2974-3460', 'Owner6', 'owner6@example.com'), (7, 'Company7', 'Address7', '010-6864-8138', 'Owner7', 'owner7@example.com'), (8, 'Company8', 'Address8', '010-5809-4253', 'Owner8', 'owner8@example.com'), (9, 'Company9', 'Address9', '010-5259-2927', 'Owner9', 'owner9@example.com'), (10, 'Company10', 'Address10', '010-7857-3541', 'Owner10', 'owner10@example.com'), (11, 'Company11', 'Address11', '010-1143-1054', 'Owner11', 'owner11@example.com'), (12, 'Company12', 'Address12', '010-5931-1161', 'Owner12', 'owner12@example.com');");
					stmt.executeUpdate("INSERT INTO camping_car VALUES (1, 'CarModel1', 'AB-1001', 4, NULL, '캠핑카 1의 설명입니다.', 71328, '2022-07-21', 11), (2, 'CarModel2', 'AB-1002', 5, NULL, '캠핑카 2의 설명입니다.', 117872, '2023-03-18', 3), (3, 'CarModel3', 'AB-1003', 2, NULL, '캠핑카 3의 설명입니다.', 121144, '2023-07-10', 4), (4, 'CarModel4', 'AB-1004', 6, NULL, '캠핑카 4의 설명입니다.', 147038, '2024-05-19', 6), (5, 'CarModel5', 'AB-1005', 5, NULL, '캠핑카 5의 설명입니다.', 77159, '2024-07-02', 7), (6, 'CarModel6', 'AB-1006', 4, NULL, '캠핑카 6의 설명입니다.', 113134, '2020-09-23', 2), (7, 'CarModel7', 'AB-1007', 8, NULL, '캠핑카 7의 설명입니다.', 129524, '2023-05-26', 9), (8, 'CarModel8', 'AB-1008', 3, NULL, '캠핑카 8의 설명입니다.', 106519, '2024-04-15', 12), (9, 'CarModel9', 'AB-1009', 6, NULL, '캠핑카 9의 설명입니다.', 109428, '2021-01-18', 5), (10, 'CarModel10', 'AB-1010', 4, NULL, '캠핑카 10의 설명입니다.', 104163, '2024-03-06', 1), (11, 'CarModel11', 'AB-1011', 7, NULL, '캠핑카 11의 설명입니다.', 128875, '2021-11-11', 10), (12, 'CarModel12', 'AB-1012', 5, NULL, '캠핑카 12의 설명입니다.', 93422, '2022-02-27', 8);");
					stmt.executeUpdate("INSERT INTO car_item VALUES (1, 'Item1', 44, 8624, '2023-02-27', 'ItemCo1'), (2, 'Item2', 73, 9328, '2021-11-14', 'ItemCo2'), (3, 'Item3', 75, 6469, '2023-07-28', 'ItemCo3'), (4, 'Item4', 80, 6887, '2024-12-30', 'ItemCo4'), (5, 'Item5', 86, 5785, '2024-06-11', 'ItemCo5'), (6, 'Item6', 33, 2692, '2022-01-02', 'ItemCo6'), (7, 'Item7', 30, 4114, '2023-03-19', 'ItemCo7'), (8, 'Item8', 57, 1072, '2020-08-31', 'ItemCo8'), (9, 'Item9', 95, 6181, '2021-09-26', 'ItemCo9'), (10, 'Item10', 65, 3124, '2022-12-06', 'ItemCo10'), (11, 'Item11', 47, 2530, '2024-08-15', 'ItemCo11'), (12, 'Item12', 46, 5421, '2023-12-06', 'ItemCo12');");
					stmt.executeUpdate("INSERT INTO car_fixlog VALUES (1, 3, '2023-05-20', 5, 197, 4), (2, 9, '2023-02-25', 1, 176, 11), (3, 4, '2024-08-11', 2, 194, 7), (4, 1, '2023-11-05', 3, 102, 8), (5, 6, '2024-11-28', 4, 143, 4), (6, 4, '2020-05-12', 2, 137, 8), (7, 4, '2020-01-27', 2, 175, 9), (8, 9, '2021-11-07', 5, 154, 12), (9, 6, '2020-05-21', 2, 100, 3), (10, 11, '2020-01-30', 2, 158, 4), (11, 12, '2022-03-22', 5, 103, 6), (12, 2, '2024-11-29', 3, 140, 5);");
					stmt.executeUpdate("INSERT INTO user VALUES (1, 1001, 'user1', 'pass1', 'User1', 'UserAddr1', '010-1307-5883', 'user1@example.com', '2022-12-21', 'AB-1006'), (2, 1002, 'user2', 'pass2', 'User2', 'UserAddr2', '010-1185-8570', 'user2@example.com', '2020-08-27', 'AB-1010'), (3, 1003, 'user3', 'pass3', 'User3', 'UserAddr3', '010-3391-2097', 'user3@example.com', '2024-06-11', 'AB-1011'), (4, 1004, 'user4', 'pass4', 'User4', 'UserAddr4', '010-5373-7883', 'user4@example.com', '2020-10-28', 'AB-1010'), (5, 1005, 'user5', 'pass5', 'User5', 'UserAddr5', '010-7861-2091', 'user5@example.com', '2022-06-26', 'AB-1012'), (6, 1006, 'user6', 'pass6', 'User6', 'UserAddr6', '010-4833-7890', 'user6@example.com', '2023-02-19', 'AB-1005'), (7, 1007, 'user7', 'pass7', 'User7', 'UserAddr7', '010-1605-6657', 'user7@example.com', '2022-02-20', 'AB-1005'), (8, 1008, 'user8', 'pass8', 'User8', 'UserAddr8', '010-1613-9773', 'user8@example.com', '2024-03-28', 'AB-1004'), (9, 1009, 'user9', 'pass9', 'User9', 'UserAddr9', '010-3779-8777', 'user9@example.com', '2021-08-17', 'AB-1002'), (10, 1010, 'user10', 'pass10', 'User10', 'UserAddr10', '010-1418-7157', 'user10@example.com', '2021-05-05', 'AB-1007'), (11, 1011, 'user11', 'pass11', 'User11', 'UserAddr11', '010-3258-4132', 'user11@example.com', '2020-03-19', 'AB-1002'), (12, 1012, 'user12', 'pass12', 'User12', 'UserAddr12', '010-2066-2583', 'user12@example.com', '2021-02-24', 'AB-1010');");
					stmt.executeUpdate("INSERT INTO rental VALUES (1, 7, 10, 6, '2022-04-06', 3, 180708, '2020-02-21', 3965, 'ETC Info 1'), (2, 9, 11, 10, '2023-08-21', 4, 152657, '2022-07-23', 633, 'ETC Info 2'), (3, 2, 3, 11, '2024-06-07', 4, 108926, '2023-12-23', 1873, 'ETC Info 3'), (4, 5, 4, 11, '2022-07-30', 7, 85850, '2020-12-03', 2527, 'ETC Info 4'), (5, 11, 10, 4, '2024-01-18', 5, 152378, '2023-08-02', 4394, 'ETC Info 5'), (6, 6, 1, 12, '2024-02-16', 3, 65328, '2023-07-22', 1220, 'ETC Info 6'), (7, 1, 7, 7, '2024-08-07', 8, 192477, '2024-10-30', 3085, 'ETC Info 7'), (8, 8, 2, 6, '2021-12-01', 8, 81931, '2024-02-07', 2129, 'ETC Info 8'), (9, 12, 8, 5, '2023-01-30', 3, 71676, '2020-02-01', 2757, 'ETC Info 9'), (10, 5, 12, 3, '2023-02-13', 4, 178900, '2022-04-05', 1508, 'ETC Info 10'), (11, 9, 8, 12, '2021-08-15', 2, 124012, '2021-11-29', 4596, 'ETC Info 11'), (12, 10, 9, 11, '2023-09-01', 8, 126265, '2021-09-06', 2242, 'ETC Info 12');");
					stmt.executeUpdate("INSERT INTO employee VALUES (1, 'Emp1', '010-1837-7580', 'EmpAddr1', 4017343, 0, 'Dept1', 'Work1'), (2, 'Emp2', '010-1880-1350', 'EmpAddr2', 5118246, 2, 'Dept2', 'Work2'), (3, 'Emp3', '010-3561-5941', 'EmpAddr3', 4923462, 2, 'Dept0', 'Work3'), (4, 'Emp4', '010-8580-1213', 'EmpAddr4', 3725077, 3, 'Dept1', 'Work0'), (5, 'Emp5', '010-5767-5672', 'EmpAddr5', 3331758, 0, 'Dept2', 'Work1'), (6, 'Emp6', '010-5599-5493', 'EmpAddr6', 4668465, 3, 'Dept0', 'Work2'), (7, 'Emp7', '010-8715-8664', 'EmpAddr7', 5520126, 1, 'Dept1', 'Work3'), (8, 'Emp8', '010-7344-8888', 'EmpAddr8', 4584690, 2, 'Dept2', 'Work0'), (9, 'Emp9', '010-5069-8726', 'EmpAddr9', 3563817, 0, 'Dept0', 'Work1'), (10, 'Emp10', '010-5882-5120', 'EmpAddr10', 3461270, 1, 'Dept1', 'Work2'), (11, 'Emp11', '010-8419-8672', 'EmpAddr11', 3602008, 2, 'Dept2', 'Work3'), (12, 'Emp12', '010-1182-8356', 'EmpAddr12', 4202541, 5, 'Dept0', 'Work0');");
					stmt.executeUpdate("INSERT INTO car_repair_shop VALUES (1, 'Shop1', 'ShopAddr1', '010-5158-4774', 'ShopOwner1', 'shopowner1@example.com'), (2, 'Shop2', 'ShopAddr2', '010-5331-6612', 'ShopOwner2', 'shopowner2@example.com'), (3, 'Shop3', 'ShopAddr3', '010-9420-6159', 'ShopOwner3', 'shopowner3@example.com'), (4, 'Shop4', 'ShopAddr4', '010-8122-2760', 'ShopOwner4', 'shopowner4@example.com'), (5, 'Shop5', 'ShopAddr5', '010-6654-7276', 'ShopOwner5', 'shopowner5@example.com'), (6, 'Shop6', 'ShopAddr6', '010-9762-3958', 'ShopOwner6', 'shopowner6@example.com'), (7, 'Shop7', 'ShopAddr7', '010-2381-4765', 'ShopOwner7', 'shopowner7@example.com'), (8, 'Shop8', 'ShopAddr8', '010-4491-9055', 'ShopOwner8', 'shopowner8@example.com'), (9, 'Shop9', 'ShopAddr9', '010-5510-9042', 'ShopOwner9', 'shopowner9@example.com'), (10, 'Shop10', 'ShopAddr10', '010-3073-5132', 'ShopOwner10', 'shopowner10@example.com'), (11, 'Shop11', 'ShopAddr11', '010-2236-4317', 'ShopOwner11', 'shopowner11@example.com'), (12, 'Shop12', 'ShopAddr12', '010-2198-4320', 'ShopOwner12', 'shopowner12@example.com');");
					stmt.executeUpdate("INSERT INTO repair VALUES (1, 1, 3, 5, 4, '엔진 점검', '2023-02-14', 32500, '2023-03-01', '정기 점검 포함'), (2, 2, 5, 1, 7, '브레이크 수리', '2022-11-20', 41200, '2022-12-05', '부품 교체 포함'), (3, 3, 2, 3, 2, '에어컨 수리', '2024-01-18', 37500, '2024-02-01', '냉매 보충'), (4, 4, 6, 10, 9, '타이어 교체', '2023-07-04', 22800, '2023-07-20', '예비 타이어 포함'), (5, 5, 8, 8, 1, '전기 배선 점검', '2021-05-27', 18000, '2021-06-05', '배터리 점검 포함'), (6, 6, 1, 6, 5, '창문 수리', '2022-04-10', 9300, '2022-04-25', '부분 수리'), (7, 7, 12, 7, 8, '핸들 교체', '2020-09-15', 28400, '2020-10-01', '소모품 포함'), (8, 8, 9, 2, 10, '엔진 오일 교환', '2024-03-29', 15200, '2024-04-10', '필터 포함'), (9, 9, 10, 4, 3, '미션 수리', '2021-08-21', 46200, '2021-09-10', '오일 포함'), (10, 10, 7, 12, 6, '도어 손잡이 교체', '2022-10-30', 18700, '2022-11-15', '좌우 모두'), (11, 11, 11, 11, 11, '냉방장치 점검', '2023-05-15', 29300, '2023-05-30', '성능저하로 점검'), (12, 12, 4, 9, 12, '히터 고장 수리', '2020-12-01', 33800, '2020-12-18', '부품 교체 포함');");
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });

        // 2. 데이터 입력 / 삭제 / 변경
        JButton insert = new JButton("입력");
        JButton del_up = new JButton("삭제/변경");
        JTextField input1 = new JTextField();
        JTextField input2 = new JTextField();
        
        JPanel crudPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        crudPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        crudPanel.add(new JLabel("테이블명:"));
        crudPanel.add(input1);
        crudPanel.add(new JLabel("조건식 입력 (WHERE ...):"));
        crudPanel.add(input2);
        crudPanel.add(insert);
        crudPanel.add(del_up);
        
        // 데이터 삽입 로직, 실행되지 않으면 안내문 출력
        insert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Statement stmt = null;
				String input = input1.getText();
				String DEL_UP = input2.getText();
				String[] parts = input.split("\\s+");  
				
				try {
					stmt = conn.createStatement();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				try {
					if(DEL_UP == "" && (parts[0] == "insert" || parts[0] == "INSERT" )) {
						stmt.executeUpdate(input);
						JOptionPane.showMessageDialog(null, "성공적으로 저장되었습니다.");
					}
					else {
						JOptionPane.showMessageDialog(null, "아래 조건을 지켜주세요\n1) 삽입은 조건절 없이 입력해주세요.\n2) 삽입문만 입력해주세요");
					}
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, "형식에 맞지 않습니다. 다시 입력해주세요.");
				}
			}
		});
        
     // 데이터 수정, 삭제 로직 맞지 않으면 안내문 띄우기
        del_up.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Statement stmt = null;
				String INSERT = input1.getText();
				String DEL_UP = input2.getText();
				String sql = INSERT + " " + DEL_UP;
				
				try {
					stmt = conn.createStatement();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				
				try {
					stmt.executeUpdate(sql);
					JOptionPane.showMessageDialog(null, "성공적으로 삭제/변경 되었습니다.");
				} catch (SQLException e1) {
					JOptionPane.showMessageDialog(null, "형식에 맞지 않습니다. 다시 입력해주세요.");
				}
			}
		});
        
        tabs.addTab("입력/삭제/변경", crudPanel);

        // 3. 전체 테이블 보기
        String[] tableNames = {
                "camping_company", "camping_car", "car_item",
                "car_fixlog", "user", "rental", "employee",
                "car_repair_shop", "repair"
            };
        
        JPanel viewAllPanel = new JPanel(new BorderLayout());
        JComboBox<String> tableSelector = new JComboBox<>(tableNames); // 테이블 선택
        JButton viewBtn = new JButton("조회");
        JTable resultTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultTable);

        viewAllPanel.add(tableSelector, BorderLayout.NORTH);
        viewAllPanel.add(viewBtn, BorderLayout.CENTER);
        viewAllPanel.add(scrollPane, BorderLayout.SOUTH);
        tabs.addTab("전체 테이블 보기", viewAllPanel);
        
        viewBtn.addActionListener(e -> {
            String selectedTable = (String) tableSelector.getSelectedItem();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM " + selectedTable)) {

                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();

                // 테이블 모델 생성
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

                resultTable.setModel(model);

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "조회 실패: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // 4. 캠핑카 및 정비 내역 보기
        JPanel vehiclePanel = new JPanel(new BorderLayout());
        JComboBox<String> vehicleBox = new JComboBox<>(new String[]{"캠핑카1", "캠핑카2"});
        JTextArea infoArea = new JTextArea(10, 40);
        vehiclePanel.add(vehicleBox, BorderLayout.NORTH);
        vehiclePanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        tabs.addTab("정비 내역 조회", vehiclePanel);

        // 5. 임의 SQL 질의 실행
        JPanel sqlPanel = new JPanel(new BorderLayout());
        JTextArea sqlInput = new JTextArea("", 5, 60);
        System.out.println(sqlInput.getText());
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
    	Connection conn = null;
		Statement stmt = null;
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver"); // MySQL 드라이버 로드
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "root",""); // JDBC 연결
			System.out.println("DB 연결 완료");

		} catch (ClassNotFoundException e) {
			System.out.println("JDBC 드라이버 로드 오류");
		} catch (SQLException e) {
			System.out.println("SQL 실행 오류");
			e.printStackTrace();
		}
		
        new AdminUI(conn);
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
