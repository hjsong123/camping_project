-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`camping_company`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`camping_company` (
  `company_id` INT NOT NULL,
  `company_name` VARCHAR(45) NOT NULL,
  `company_address` VARCHAR(45) NOT NULL,
  `company_phone` VARCHAR(45) NOT NULL,
  `company_oner` VARCHAR(45) NOT NULL,
  `company_oner_email` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`company_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`camping_car`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`camping_car` (
  `car_id` INT NOT NULL,
  `car_name` VARCHAR(45) NOT NULL,
  `car_number` VARCHAR(45) NOT NULL,
  `car_person` INT NOT NULL,
  `car_image` BLOB NULL,
  `car_info` TEXT(100) NULL,
  `car_price` INT NOT NULL,
  `car_date` DATE NOT NULL,
  `car_company_id` INT NOT NULL,
  PRIMARY KEY (`car_id`),
  INDEX `car_company_id_idx` (`car_company_id` ASC) VISIBLE,
  CONSTRAINT `car_company_id`
    FOREIGN KEY (`car_company_id`)
    REFERENCES `mydb`.`camping_company` (`company_id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`car_item_`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`car_item` (
  `item_id` INT NOT NULL,
  `item_name` VARCHAR(45) NOT NULL,
  `item_price` INT NOT NULL,
  `item_stock` INT NOT NULL,
  `item_last_store` DATE NOT NULL,
  `item_store_company` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`item_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`car_fixlog`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`car_fixlog` (
  `fix_id` INT NOT NULL,
  `fix_car_id` INT NOT NULL,
  `fix_date` DATE NOT NULL,
  `fix_time` INT NOT NULL,
  `fix_fixer_id` INT NOT NULL,
  `fix_item_id` INT NOT NULL,
  PRIMARY KEY (`fix_id`),
  INDEX `fix_car_id_idx` (`fix_car_id` ASC) VISIBLE,
  INDEX `fix_item_id_idx` (`fix_item_id` ASC) VISIBLE,
  CONSTRAINT `fix_car_id`
    FOREIGN KEY (`fix_car_id`)
    REFERENCES `mydb`.`camping_car` (`car_id`)
    ON DELETE CASCADE
    ON UPDATE RESTRICT,
  CONSTRAINT `fix_item_id`
    FOREIGN KEY (`fix_item_id`)
    REFERENCES `mydb`.`car_item_` (`item_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`user` (
  `user_id` INT NOT NULL,
  `user_license` INT NOT NULL,
  `user_account` VARCHAR(45) NOT NULL,
  `user_password` VARCHAR(45) NOT NULL,
  `user_name` VARCHAR(45) NULL,
  `user_address` VARCHAR(45) NULL,
  `user_phone` VARCHAR(45) NULL,
  `user_email` VARCHAR(45) NULL,
  `user_last_use_day` DATE NULL,
  `user_last_use_car` VARCHAR(45) NULL,
  PRIMARY KEY (`user_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`rental`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`rental` (
  `rental_id` INT NOT NULL,
  `rental_car_id` INT NOT NULL,
  `rental_car_company_id` INT NOT NULL,
  `rental_user_license` INT NOT NULL,
  `rental_start_day` DATE NOT NULL,
  `rental_during` INT NOT NULL,
  `rental_price` INT NOT NULL,
  `rental_payment_due_date` DATE NOT NULL,
  `rental_etc_price` VARCHAR(45) NULL,
  `rental_etc_price_info` TEXT(100) NULL,
  PRIMARY KEY (`rental_id`),
  INDEX `rental_car_id_idx` (`rental_car_id` ASC) VISIBLE,
  INDEX `rental_car_company_idx` (`rental_car_company_id` ASC) VISIBLE,
  INDEX `rental_user_license_idx` (`rental_user_license` ASC) VISIBLE,
  CONSTRAINT `rental_car_id`
    FOREIGN KEY (`rental_car_id`)
    REFERENCES `mydb`.`camping_car` (`car_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `rental_car_company_id`
    FOREIGN KEY (`rental_car_company_id`)
    REFERENCES `mydb`.`camping_company` (`company_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `rental_user_license`
    FOREIGN KEY (`rental_user_license`)
    REFERENCES `mydb`.`user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`employee`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`employee` (
  `employee_id` INT NOT NULL,
  `employee_name` VARCHAR(45) NOT NULL,
  `employee_phone` VARCHAR(45) NOT NULL,
  `employee_address` VARCHAR(45) NOT NULL,
  `employee_salary` INT NULL,
  `employee_family` INT NULL,
  `employee_department` VARCHAR(45) NULL,
  `employee_work` VARCHAR(45) NULL,
  PRIMARY KEY (`employee_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`car_repair_shop`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`car_repair_shop` (
  `repair_shop_id` INT NOT NULL,
  `repair_shop_name` VARCHAR(45) NOT NULL,
  `repair_shop_address` VARCHAR(45) NOT NULL,
  `repair_shop_phone` VARCHAR(45) NOT NULL,
  `repair_shop_oner` VARCHAR(45) NOT NULL,
  `repair_shop_oner_email` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`repair_shop_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`repair`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`repair` (
  `repair_id` INT NOT NULL,
  `repair_car_id` INT NULL,
  `repair_repair_shop_id` INT NULL,
  `repair_car_company_id` INT NULL,
  `repair_user_license` INT NULL,
  `repair_content` VARCHAR(45) NULL,
  `repair_day` DATE NULL,
  `repair_price` INT NULL,
  `repair_payment_due_date` DATE NULL,
  `repair_etc_info` VARCHAR(45) NULL,
  PRIMARY KEY (`repair_id`),
  INDEX `rental_car_company_id_idx` (`repair_car_company_id` ASC) VISIBLE,
  INDEX `rental_repair_shop_id_idx` (`repair_repair_shop_id` ASC) VISIBLE,
  INDEX `rental_user_license_idx` (`repair_user_license` ASC) VISIBLE,
  INDEX `ｒｅｐａｉｒ＿ｃａｒ＿ｉｄ_idx` (`repair_car_id` ASC) VISIBLE,
  CONSTRAINT `ｒｅｐａｉｒ＿ｃａｒ＿ｉｄ`
    FOREIGN KEY (`repair_car_id`)
    REFERENCES `mydb`.`car_item` (`item_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `ｒｅｐａｉｒ_car_company_id`
    FOREIGN KEY (`repair_car_company_id`)
    REFERENCES `mydb`.`camping_company` (`company_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `ｒｅｐａｉｒ_repair_shop_id`
    FOREIGN KEY (`repair_repair_shop_id`)
    REFERENCES `mydb`.`car_repair_shop` (`repair_shop_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `rｅｐａｉｒ_user_license`
    FOREIGN KEY (`repair_user_license`)
    REFERENCES `mydb`.`user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- Camping Companies
INSERT INTO camping_company 
VALUES 
(1, 'Company1', 'Address1', '010-6549-3690', 'Owner1', 'owner1@example.com'),
(2, 'Company2', 'Address2', '010-2015-3208', 'Owner2', 'owner2@example.com'),
(3, 'Company3', 'Address3', '010-2934-2966', 'Owner3', 'owner3@example.com'),
(4, 'Company4', 'Address4', '010-7503-4243', 'Owner4', 'owner4@example.com'),
(5, 'Company5', 'Address5', '010-9388-1276', 'Owner5', 'owner5@example.com'),
(6, 'Company6', 'Address6', '010-2974-3460', 'Owner6', 'owner6@example.com'),
(7, 'Company7', 'Address7', '010-6864-8138', 'Owner7', 'owner7@example.com'),
(8, 'Company8', 'Address8', '010-5809-4253', 'Owner8', 'owner8@example.com'),
(9, 'Company9', 'Address9', '010-5259-2927', 'Owner9', 'owner9@example.com'),
(10, 'Company10', 'Address10', '010-7857-3541', 'Owner10', 'owner10@example.com'),
(11, 'Company11', 'Address11', '010-1143-1054', 'Owner11', 'owner11@example.com'),
(12, 'Company12', 'Address12', '010-5931-1161', 'Owner12', 'owner12@example.com');

-- Camping Cars
INSERT INTO camping_car 
VALUES 
(1, 'CarModel1', 'AB-1001', 4, NULL, '캠핑카 1의 설명입니다.', 71328, '2022-07-21', 11),
(2, 'CarModel2', 'AB-1002', 5, NULL, '캠핑카 2의 설명입니다.', 117872, '2023-03-18', 3),
(3, 'CarModel3', 'AB-1003', 2, NULL, '캠핑카 3의 설명입니다.', 121144, '2023-07-10', 4),
(4, 'CarModel4', 'AB-1004', 6, NULL, '캠핑카 4의 설명입니다.', 147038, '2024-05-19', 6),
(5, 'CarModel5', 'AB-1005', 5, NULL, '캠핑카 5의 설명입니다.', 77159, '2024-07-02', 7),
(6, 'CarModel6', 'AB-1006', 4, NULL, '캠핑카 6의 설명입니다.', 113134, '2020-09-23', 2),
(7, 'CarModel7', 'AB-1007', 8, NULL, '캠핑카 7의 설명입니다.', 129524, '2023-05-26', 9),
(8, 'CarModel8', 'AB-1008', 3, NULL, '캠핑카 8의 설명입니다.', 106519, '2024-04-15', 12),
(9, 'CarModel9', 'AB-1009', 6, NULL, '캠핑카 9의 설명입니다.', 109428, '2021-01-18', 5),
(10, 'CarModel10', 'AB-1010', 4, NULL, '캠핑카 10의 설명입니다.', 104163, '2024-03-06', 1),
(11, 'CarModel11', 'AB-1011', 7, NULL, '캠핑카 11의 설명입니다.', 128875, '2021-11-11', 10),
(12, 'CarModel12', 'AB-1012', 5, NULL, '캠핑카 12의 설명입니다.', 93422, '2022-02-27', 8);

-- Car Items
INSERT INTO car_item 
VALUES 
(1, 'Item1', 44, 8624, '2023-02-27', 'ItemCo1'),
(2, 'Item2', 73, 9328, '2021-11-14', 'ItemCo2'),
(3, 'Item3', 75, 6469, '2023-07-28', 'ItemCo3'),
(4, 'Item4', 80, 6887, '2024-12-30', 'ItemCo4'),
(5, 'Item5', 86, 5785, '2024-06-11', 'ItemCo5'),
(6, 'Item6', 33, 2692, '2022-01-02', 'ItemCo6'),
(7, 'Item7', 30, 4114, '2023-03-19', 'ItemCo7'),
(8, 'Item8', 57, 1072, '2020-08-31', 'ItemCo8'),
(9, 'Item9', 95, 6181, '2021-09-26', 'ItemCo9'),
(10, 'Item10', 65, 3124, '2022-12-06', 'ItemCo10'),
(11, 'Item11', 47, 2530, '2024-08-15', 'ItemCo11'),
(12, 'Item12', 46, 5421, '2023-12-06', 'ItemCo12');

-- Car Fix Logs
SET foreign_key_checks = 0; -- 외래키 무시하고 생성하기 에러코드 1452 해결 코드
INSERT INTO car_fixlog 
VALUES 
(1, 3, '2023-05-20', 5, 197, 4),
(2, 9, '2023-02-25', 1, 176, 11),
(3, 4, '2024-08-11', 2, 194, 7),
(4, 1, '2023-11-05', 3, 102, 8),
(5, 6, '2024-11-28', 4, 143, 4),
(6, 4, '2020-05-12', 2, 137, 8),
(7, 4, '2020-01-27', 2, 175, 9),
(8, 9, '2021-11-07', 5, 154, 12),
(9, 6, '2020-05-21', 2, 100, 3),
(10, 11, '2020-01-30', 2, 158, 4),
(11, 12, '2022-03-22', 5, 103, 6),
(12, 2, '2024-11-29', 3, 140, 5);
SET foreign_key_checks = 1;
-- Users
INSERT INTO user 
VALUES 
(1, 1001, 'user1', 'pass1', 'User1', 'UserAddr1', '010-1307-5883', 'user1@example.com', '2022-12-21', 'AB-1006'),
(2, 1002, 'user2', 'pass2', 'User2', 'UserAddr2', '010-1185-8570', 'user2@example.com', '2020-08-27', 'AB-1010'),
(3, 1003, 'user3', 'pass3', 'User3', 'UserAddr3', '010-3391-2097', 'user3@example.com', '2024-06-11', 'AB-1011'),
(4, 1004, 'user4', 'pass4', 'User4', 'UserAddr4', '010-5373-7883', 'user4@example.com', '2020-10-28', 'AB-1010'),
(5, 1005, 'user5', 'pass5', 'User5', 'UserAddr5', '010-7861-2091', 'user5@example.com', '2022-06-26', 'AB-1012'),
(6, 1006, 'user6', 'pass6', 'User6', 'UserAddr6', '010-4833-7890', 'user6@example.com', '2023-02-19', 'AB-1005'),
(7, 1007, 'user7', 'pass7', 'User7', 'UserAddr7', '010-1605-6657', 'user7@example.com', '2022-02-20', 'AB-1005'),
(8, 1008, 'user8', 'pass8', 'User8', 'UserAddr8', '010-1613-9773', 'user8@example.com', '2024-03-28', 'AB-1004'),
(9, 1009, 'user9', 'pass9', 'User9', 'UserAddr9', '010-3779-8777', 'user9@example.com', '2021-08-17', 'AB-1002'),
(10, 1010, 'user10', 'pass10', 'User10', 'UserAddr10', '010-1418-7157', 'user10@example.com', '2021-05-05', 'AB-1007'),
(11, 1011, 'user11', 'pass11', 'User11', 'UserAddr11', '010-3258-4132', 'user11@example.com', '2020-03-19', 'AB-1002'),
(12, 1012, 'user12', 'pass12', 'User12', 'UserAddr12', '010-2066-2583', 'user12@example.com', '2021-02-24', 'AB-1010');

-- Rentals
INSERT INTO rental 
VALUES 
(1, 7, 10, 6, '2022-04-06', 3, 180708, '2020-02-21', 3965, 'ETC Info 1'),
(2, 9, 11, 10, '2023-08-21', 4, 152657, '2022-07-23', 633, 'ETC Info 2'),
(3, 2, 3, 11, '2024-06-07', 4, 108926, '2023-12-23', 1873, 'ETC Info 3'),
(4, 5, 4, 11, '2022-07-30', 7, 85850, '2020-12-03', 2527, 'ETC Info 4'),
(5, 11, 10, 4, '2024-01-18', 5, 152378, '2023-08-02', 4394, 'ETC Info 5'),
(6, 6, 1, 12, '2024-02-16', 3, 65328, '2023-07-22', 1220, 'ETC Info 6'),
(7, 1, 7, 7, '2024-08-07', 8, 192477, '2024-10-30', 3085, 'ETC Info 7'),
(8, 8, 2, 6, '2021-12-01', 8, 81931, '2024-02-07', 2129, 'ETC Info 8'),
(9, 12, 8, 5, '2023-01-30', 3, 71676, '2020-02-01', 2757, 'ETC Info 9'),
(10, 5, 12, 3, '2023-02-13', 4, 178900, '2022-04-05', 1508, 'ETC Info 10'),
(11, 9, 8, 12, '2021-08-15', 2, 124012, '2021-11-29', 4596, 'ETC Info 11'),
(12, 10, 9, 11, '2023-09-01', 8, 126265, '2021-09-06', 2242, 'ETC Info 12');

-- Employees
INSERT INTO employee 
VALUES 
(1, 'Emp1', '010-1837-7580', 'EmpAddr1', 4017343, 0, 'Dept1', 'Work1'),
(2, 'Emp2', '010-1880-1350', 'EmpAddr2', 5118246, 2, 'Dept2', 'Work2'),
(3, 'Emp3', '010-3561-5941', 'EmpAddr3', 4923462, 2, 'Dept0', 'Work3'),
(4, 'Emp4', '010-8580-1213', 'EmpAddr4', 3725077, 3, 'Dept1', 'Work0'),
(5, 'Emp5', '010-5767-5672', 'EmpAddr5', 3331758, 0, 'Dept2', 'Work1'),
(6, 'Emp6', '010-5599-5493', 'EmpAddr6', 4668465, 3, 'Dept0', 'Work2'),
(7, 'Emp7', '010-8715-8664', 'EmpAddr7', 5520126, 1, 'Dept1', 'Work3'),
(8, 'Emp8', '010-7344-8888', 'EmpAddr8', 4584690, 2, 'Dept2', 'Work0'),
(9, 'Emp9', '010-5069-8726', 'EmpAddr9', 3563817, 0, 'Dept0', 'Work1'),
(10, 'Emp10', '010-5882-5120', 'EmpAddr10', 3461270, 1, 'Dept1', 'Work2'),
(11, 'Emp11', '010-8419-8672', 'EmpAddr11', 3602008, 2, 'Dept2', 'Work3'),
(12, 'Emp12', '010-1182-8356', 'EmpAddr12', 4202541, 5, 'Dept0', 'Work0');

-- Car Repair Shops
INSERT INTO car_repair_shop 
VALUES 
(1, 'Shop1', 'ShopAddr1', '010-5158-4774', 'ShopOwner1', 'shopowner1@example.com'),
(2, 'Shop2', 'ShopAddr2', '010-5331-6612', 'ShopOwner2', 'shopowner2@example.com'),
(3, 'Shop3', 'ShopAddr3', '010-9420-6159', 'ShopOwner3', 'shopowner3@example.com'),
(4, 'Shop4', 'ShopAddr4', '010-8122-2760', 'ShopOwner4', 'shopowner4@example.com'),
(5, 'Shop5', 'ShopAddr5', '010-6654-7276', 'ShopOwner5', 'shopowner5@example.com'),
(6, 'Shop6', 'ShopAddr6', '010-9762-3958', 'ShopOwner6', 'shopowner6@example.com'),
(7, 'Shop7', 'ShopAddr7', '010-2381-4765', 'ShopOwner7', 'shopowner7@example.com'),
(8, 'Shop8', 'ShopAddr8', '010-4491-9055', 'ShopOwner8', 'shopowner8@example.com'),
(9, 'Shop9', 'ShopAddr9', '010-5510-9042', 'ShopOwner9', 'shopowner9@example.com'),
(10, 'Shop10', 'ShopAddr10', '010-3073-5132', 'ShopOwner10', 'shopowner10@example.com'),
(11, 'Shop11', 'ShopAddr11', '010-2236-4317', 'ShopOwner11', 'shopowner11@example.com'),
(12, 'Shop12', 'ShopAddr12', '010-2198-4320', 'ShopOwner12', 'shopowner12@example.com');

-- Repairs
INSERT INTO repair 
VALUES 
(1, 1, 3, 5, 4, '엔진 점검', '2023-02-14', 32500, '2023-03-01', '정기 점검 포함'),
(2, 2, 5, 1, 7, '브레이크 수리', '2022-11-20', 41200, '2022-12-05', '부품 교체 포함'),
(3, 3, 2, 3, 2, '에어컨 수리', '2024-01-18', 37500, '2024-02-01', '냉매 보충'),
(4, 4, 6, 10, 9, '타이어 교체', '2023-07-04', 22800, '2023-07-20', '예비 타이어 포함'),
(5, 5, 8, 8, 1, '전기 배선 점검', '2021-05-27', 18000, '2021-06-05', '배터리 점검 포함'),
(6, 6, 1, 6, 5, '창문 수리', '2022-04-10', 9300, '2022-04-25', '부분 수리'),
(7, 7, 12, 7, 8, '핸들 교체', '2020-09-15', 28400, '2020-10-01', '소모품 포함'),
(8, 8, 9, 2, 10, '엔진 오일 교환', '2024-03-29', 15200, '2024-04-10', '필터 포함'),
(9, 9, 10, 4, 3, '미션 수리', '2021-08-21', 46200, '2021-09-10', '오일 포함'),
(10, 10, 7, 12, 6, '도어 손잡이 교체', '2022-10-30', 18700, '2022-11-15', '좌우 모두'),
(11, 11, 11, 11, 11, '냉방장치 점검', '2023-05-15', 29300, '2023-05-30', '성능저하로 점검'),
(12, 12, 4, 9, 12, '히터 고장 수리', '2020-12-01', 33800, '2020-12-18', '부품 교체 포함');

SELECT * FROM Camping_car;
SELECT * FROM Camping_company;
SELECT * FROM Car_item;
SELECT * FROM Car_fixlog;
SELECT * FROM user;
SELECT * FROM rental;
SELECT * FROM employee;
SELECT * FROM car_repair_shop;
SELECT * FROM repair;

-- 지우는 순서는 어지간하면 바꾸지 말 것! => 참조 관계 따라 우선 지워야하는 것이 있음.
delete from employee
where employee_id < 13;

delete from repair
where repair_id < 13;

delete from car_repair_shop
where repair_shop_id < 13;

delete from rental
where rental_id < 13;

delete from car_item
where item_id < 13;

delete from car_fixlog
where fix_id < 13;

delete from camping_car
where car_id < 13;

delete from camping_company
where company_id < 13;

delete from User
where User_id < 13;
-- 지우는 순서는 어지간하면 바꾸지 말 것! => 참조 관계 따라 우선 지워야하는 것이 있음.

-- drop database mydb; 
