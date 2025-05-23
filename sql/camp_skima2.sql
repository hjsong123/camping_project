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
CREATE SCHEMA IF NOT EXISTS `mydb` ;
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
  `car_image` BLOB NULL DEFAULT NULL,
  `car_info` TEXT NOT NULL,
  `car_price` INT NOT NULL,
  `car_date` DATE NOT NULL,
  `car_company_id` INT NOT NULL,
  PRIMARY KEY (`car_id`),
  INDEX `car_company_id_idx` (`car_company_id` ASC) VISIBLE,
  CONSTRAINT `car_company_id`
    FOREIGN KEY (`car_company_id`)
    REFERENCES `mydb`.`camping_company` (`company_id`)
    ON DELETE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`car_item`
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
    ON DELETE CASCADE,
  CONSTRAINT `fix_item_id`
    FOREIGN KEY (`fix_item_id`)
    REFERENCES `mydb`.`car_item` (`item_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
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
-- Table `mydb`.`employee`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`employee` (
  `employee_id` INT NOT NULL,
  `employee_name` VARCHAR(45) NOT NULL,
  `employee_phone` VARCHAR(45) NOT NULL,
  `employee_address` VARCHAR(45) NOT NULL,
  `employee_salary` INT NOT NULL,
  `employee_family` INT NOT NULL,
  `employee_department` VARCHAR(45) NOT NULL,
  `employee_work` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`employee_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`user` (
  `user_id` INT NOT NULL,
  `user_license` INT NOT NULL,
  `user_account` VARCHAR(45) NOT NULL,
  `user_password` VARCHAR(45) NOT NULL,
  `user_name` VARCHAR(45) NOT NULL,
  `user_address` VARCHAR(45) NOT NULL,
  `user_phone` VARCHAR(45) NOT NULL,
  `user_email` VARCHAR(45) NOT NULL,
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
  `rental_etc_price` VARCHAR(45) NULL DEFAULT NULL,
  `rental_etc_price_info` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`rental_id`),
  INDEX `rental_car_id_idx` (`rental_car_id` ASC) VISIBLE,
  INDEX `rental_car_company_idx` (`rental_car_company_id` ASC) VISIBLE,
  INDEX `rental_user_license_idx` (`rental_user_license` ASC) VISIBLE,
  CONSTRAINT `rental_car_company_id`
    FOREIGN KEY (`rental_car_company_id`)
    REFERENCES `mydb`.`camping_company` (`company_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `rental_car_id`
    FOREIGN KEY (`rental_car_id`)
    REFERENCES `mydb`.`camping_car` (`car_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `rental_user_license`
    FOREIGN KEY (`rental_user_license`)
    REFERENCES `mydb`.`user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`repair`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `mydb`.`repair` (
  `repair_id` INT NOT NULL,
  `repair_car_id` INT NOT NULL,
  `repair_repair_shop_id` INT NOT NULL,
  `repair_car_company_id` INT NOT NULL,
  `repair_user_license` INT NOT NULL,
  `repair_content` VARCHAR(45) NOT NULL,
  `repair_day` DATE NOT NULL,
  `repair_price` INT NOT NULL,
  `repair_payment_due_date` DATE NOT NULL,
  `repair_etc_info` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`repair_id`),
  INDEX `rental_car_company_id_idx` (`repair_car_company_id` ASC) VISIBLE,
  INDEX `rental_repair_shop_id_idx` (`repair_repair_shop_id` ASC) VISIBLE,
  INDEX `rental_user_license_idx` (`repair_user_license` ASC) VISIBLE,
  INDEX `ｒｅｐａｉｒ＿ｃａｒ＿ｉｄ_idx` (`repair_car_id` ASC) VISIBLE,
  CONSTRAINT `rｅｐａｉｒ_user_license`
    FOREIGN KEY (`repair_user_license`)
    REFERENCES `mydb`.`user` (`user_id`)
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
  CONSTRAINT `ｒｅｐａｉｒ＿ｃａｒ＿ｉｄ`
    FOREIGN KEY (`repair_car_id`)
    REFERENCES `mydb`.`car_item` (`item_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
