CREATE TABLE IF NOT EXISTS aoe_donate (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT  NOT NULL,
    username VARCHAR(256) NOT NULL,
    nick_name VARCHAR(256) NOT NULL,
    phone varchar(256) NOT NULL,
    amount INT  NOT NULL,
    service INT NOT NULL,
    target_id BIGINT NOT NULL,
    message VARCHAR(256) NOT NULL DEFAULT '',
    sub_star_transaction_id BIGINT NOT NULL,
    add_star_transaction_id BIGINT NOT NULL DEFAULT 0,
    state INT NOT NULL DEFAULT 0,
    create_time BIGINT  NOT NULL
);

