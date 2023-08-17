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
    state INT NOT NULL DEFAULT 0,
    refer_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS bank_transaction
(
    id                    BIGINT       PRIMARY KEY AUTO_INCREMENT,
    bank_transaction_id   VARCHAR(50)              NOT NULL,
    receiver_bankcode     INT          DEFAULT 0   NOT NULL,
    receiver_bank_account VARCHAR(50)  DEFAULT ''  NOT NULL,
    receiver_bank_name    VARCHAR(200) DEFAULT ''  NOT NULL,
    receiver_name         VARCHAR(50)  DEFAULT ''  NOT NULL,
    note                  TEXT         DEFAULT ''  NOT NULL,
    amount                BIGINT       DEFAULT 0   NOT NULL,
    sender_bankcode       INT          DEFAULT 0   NOT NULL,
    sender_bank_account   VARCHAR(50)  DEFAULT ''  NOT NULL,
    sender_bank_name      VARCHAR(252) DEFAULT ''  NOT NULL,
    sender_name           VARCHAR(252) DEFAULT ''  NOT NULL,
    create_time           BIGINT       DEFAULT 0   NOT NULL,
    status                INT          DEFAULT 0   NOT NULL,
    star_transaction_id   BIGINT       DEFAULT -1  NOT NULL
);
CREATE INDEX bank_transaction_create_time_index ON bank_transaction (create_time);

