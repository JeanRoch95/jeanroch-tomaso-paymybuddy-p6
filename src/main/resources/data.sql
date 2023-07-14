INSERT INTO user(email,firstname,lastname,password,balance) values('Jhon@mail.com','John','Doe','password',502);
INSERT INTO user(email,firstname,lastname,password,balance) values('jane@mail.com','Jane','Doe','password',100);

INSERT INTO bank(iban, swift) values('LU76 4567 8790 8798 789056781', 'AXAB BE 22 XXX');
INSERT INTO bank(iban, swift) values('FR76 4567 8790 8798 789056781', 'AXAB BE 22 XXX');

INSERT INTO bank_transfer(amount, description) values (124, 'restaurant');
INSERT INTO bank_transfer(amount, description) values (34.6, 'restaurant');