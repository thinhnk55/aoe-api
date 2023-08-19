CREATE TABLE IF NOT EXISTS timo_account
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    username VARCHAR(64) UNIQUE NOT NULL ,
    password VARCHAR(2048) NOT NULL,
    device VARCHAR(128),
    token VARCHAR(2048),
    bank_account_id BIGINT NOT NULL DEFAULT 0,
    other VARCHAR(4096),
    state INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS bank_account
(
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    bank_code INT NOT NULL DEFAULT 0,
    account_number VARCHAR(32),
    account_owner VARCHAR(256),
    state INT NOT NULL DEFAULT 0,
    bank_detail_id BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS bank_transaction
(
    id                    BIGINT       PRIMARY KEY AUTO_INCREMENT,
    bank_transaction_id   VARCHAR(64)              NOT NULL,
    receiver_bankcode     INT          DEFAULT 0   NOT NULL,
    receiver_bank_account VARCHAR(64)  DEFAULT ''  NOT NULL,
    receiver_name         VARCHAR(64)  DEFAULT ''  NOT NULL,
    note                  VARCHAR(256)         DEFAULT ''  NOT NULL,
    amount                BIGINT       DEFAULT 0   NOT NULL,
    sender_bankcode       INT          DEFAULT 0   NOT NULL,
    sender_bank_account   VARCHAR(64)  DEFAULT ''  NOT NULL,
    sender_name           VARCHAR(256) DEFAULT ''  NOT NULL,
    create_time           BIGINT       DEFAULT 0   NOT NULL,
    state                INT          DEFAULT 0   NOT NULL,
    star_transaction_id   BIGINT       DEFAULT 0  NOT NULL,
    service   INT       DEFAULT 0  NOT NULL,
    target_id   INT       DEFAULT 0  NOT NULL
);
CREATE INDEX bank_transaction_create_time_index ON bank_transaction (create_time);

