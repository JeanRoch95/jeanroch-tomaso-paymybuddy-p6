INSERT INTO user(email,firstname,lastname,password,balance) values('Jhon@mail.com','John','Doe','password',502);

INSERT INTO bank_account(name,iban,swift,user_id,created_at) values('JR','FR76 3333 3333 3333 333333333', 'JRTT SS 45',1, NOW());
INSERT INTO bank_account(name,iban,swift,user_id,created_at) values('JP','LHR86 5555 5555 5555 555555555', 'JRTT SS 45',1, NOW());

INSERT INTO bank_transfer(amount, description, created_at, bank_account_id) values (124, 'restaurant', '2023-10-01 10:31:30.0', 1);
INSERT INTO bank_transfer(amount, description, created_at, bank_account_id) values (34.6, 'restaurant', '2022-10-01 10:32:01.0', 2);

