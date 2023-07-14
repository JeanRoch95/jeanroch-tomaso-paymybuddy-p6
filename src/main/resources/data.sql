INSERT INTO user(email,firstname,lastname,password,balance) values('Jhon@mail.com','John','Doe','password',502);
INSERT INTO user(email,firstname,lastname,password,balance) values('jane@mail.com','Jane','Doe','password',100);

INSERT INTO bank(iban,swift,user_id) values('FR76 3333 3333 3333 333333333', 'JRTT SS 45 ZZZ',1);
INSERT INTO bank(iban,swift,user_id) values('LHR86 5555 5555 5555 555555555', 'JRTT SS 45 ZZZ',1);
INSERT INTO bank(iban,swift,user_id) values('GD74 4444 4444 4444 444444444', 'ZEFR OO 10 ZZZ',2);

INSERT INTO bank_transfer(amount, description, created_at, bank_id) values (124, 'restaurant', '2023-10-01 10:31:30.0', 3);
INSERT INTO bank_transfer(amount, description, created_at, bank_id) values (34.6, 'restaurant', '2022-10-01 10:32:01.0', 2);

