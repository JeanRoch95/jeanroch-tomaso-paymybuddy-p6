INSERT INTO user(email,firstname,lastname,password,balance, created_at) values('Jhon@mail.com','John','Doe','password',502, CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+00:00'));
INSERT INTO user(email,firstname,lastname,password,balance, created_at) values('JR@mail.com','JR','Doe','password',800, CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+00:00'));
INSERT INTO user(email,firstname,lastname,password,balance, created_at) values('JP@mail.com','JP','Deos','password',100, CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+00:00'));

INSERT INTO bank_account(name,iban,swift,user_id,created_at) values('JR','FR76 3333 3333 3333 333333333', 'JRTT SS 45',1, CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+00:00'));
INSERT INTO bank_account(name,iban,swift,user_id,created_at) values('JP','LHR86 5555 5555 5555 555555555', 'JRTT SS 45',1, CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+00:00'));
INSERT INTO bank_account(name,iban,swift,user_id,created_at) values('JP','LHR86 5555 5555 5555 555555555', 'JRTT SS 45',2, CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+00:00'));

INSERT INTO bank_transfer(amount, description, created_at, type, bank_account_id) values (124, 'restaurant', CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+00:00'), 'debit', 1);
INSERT INTO bank_transfer(amount, description, created_at, type, bank_account_id) values (34.6, 'restaurant', CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+00:00'), 'credit', 2);
