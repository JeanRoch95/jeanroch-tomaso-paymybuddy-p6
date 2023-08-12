INSERT INTO user(email,firstname,lastname,password,balance, created_at) values('Jhon@mail.com','John','Doe','$2a$10$JW/K8QTRelvqEy4byBXf9e.nUpE7BX.oTT85KMVwPWzK8i3dFoCGy',502, CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+00:00'));
INSERT INTO user(email,firstname,lastname,password,balance, created_at) values('JR@mail.com','JR','Doe','$2a$10$JW/K8QTRelvqEy4byBXf9e.nUpE7BX.oTT85KMVwPWzK8i3dFoCGy',800, CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+00:00'));
INSERT INTO user(email,firstname,lastname,password,balance, created_at) values('JP@mail.com','JP','Deos','$2a$10$JW/K8QTRelvqEy4byBXf9e.nUpE7BX.oTT85KMVwPWzK8i3dFoCGy',100, CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+00:00'));

INSERT INTO bank_account(name,iban,swift,user_id,created_at) values('JR','FR76 3333 3333 3333 333333333', 'JRTT SS 45',1, CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+00:00'));
INSERT INTO bank_account(name,iban,swift,user_id,created_at) values('JP','LHR86 5555 5555 5555 555555555', 'JRTT SS 45',1, CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+00:00'));
INSERT INTO bank_account(name,iban,swift,user_id,created_at) values('JP','LHR86 5555 5555 5555 555555555', 'JRTT SS 45',2, CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+00:00'));

INSERT INTO bank_transfer(amount, description, created_at, type, bank_account_id) values (124, 'restaurant', CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+00:00'), 'debit', 1);
INSERT INTO bank_transfer(amount, description, created_at, type, bank_account_id) values (34.6, 'restaurant', CONVERT_TZ(UTC_TIMESTAMP(), '+00:00', '+00:00'), 'credit', 2);

CREATE TABLE persistent_logins (
                                   username VARCHAR(64) NOT NULL,
                                   series VARCHAR(64) PRIMARY KEY,
                                   token VARCHAR(64) NOT NULL,
                                   last_used TIMESTAMP NOT NULL
);