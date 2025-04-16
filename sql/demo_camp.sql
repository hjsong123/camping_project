create database dev_campDB;
commit;

USE dev_campDB;

CREATE TABLE User(
	user_id		INTEGER PRIMARY KEY,
    user_name 	varchar(25)
);

INSERT into User values (1, '송하종');

SELECT * FROM User;