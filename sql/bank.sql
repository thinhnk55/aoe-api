CREATE TABLE IF NOT EXISTS timo_account
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL ,
    password VARCHAR(2048) NOT NULL,
    device VARCHAR(50),
    token VARCHAR(2048),
    transaction_reference VARCHAR(50), #Ma giao dich cuoi cung truoc khi logout
    logout_time BIGINT, #Ma giao dich cuoi cung truoc khi logout
    bank_account_id BIGINT,
    state INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS bank_account
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    bank_code INT NOT NULL DEFAULT 0,
    account_number VARCHAR(50),
    account_owner VARCHAR(50),
    state INT NOT NULL DEFAULT 0
);
