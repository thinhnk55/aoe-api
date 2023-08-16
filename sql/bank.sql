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

create table bank_transaction
(
    id                    PRIMARY KEY  BIGINT      AUTO_INCREMENT,
    refNo                 VARCHAR(50)              not null,
    transaction_id        BIGINT,
    receiver_bankcode     INT          default 0   not null,
    receiver_bank_account VARCHAR(50)  default '0' not null,
    receiver_bank_name    VARCHAR(200) default '0' not null,
    receiver_name         VARCHAR(50)  default '0' not null,
    note                  TEXT                     not null,
    amount                BIGINT       default 0   not null,
    sender_bankcode       INT          default 0   not null,
    sender_bank_account   VARCHAR(50)  default '0' not null,
    sender_bank_name      VARCHAR(252) default '0' not null,
    sender_name           VARCHAR(252) default '0' not null,
    create_time           BIGINT       default 0   not null,
    status                INT          default 0   not null
)

create index bank_trans_create_time_index
    on bank_transaction (create_time);

