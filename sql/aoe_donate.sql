CREATE TABLE IF NOT EXISTS aoe_match_donate (
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id BIGINT  NOT NULL,
    user_name VARCHAR(256) NOT NULL,
    nick_name VARCHAR(256) NOT NULL,
    phone_number VARCHAR(256) NOT NULL,
    amount INT  NOT NULL,
    match_id BIGINT NOT NULL,
    message VARCHAR(256) NOT NULL,
    create_time BIGINT  NOT NULL
);

CREATE TABLE IF NOT EXISTS aoe_donate_gamer (
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    sender_id BIGINT NOT NULL,
    sender_user_name VARCHAR(256) NOT NULL,
    sender_nick_name VARCHAR(256) NOT NULL,
    sender_phone VARCHAR(256) NOT NULL,
    amount INT NOT NULL,
    message VARCHAR(256) NOT NULL,
    service int NOT NULL,           #11: donate for gamer,   12: donate for caster
    receiver_id BIGINT NOT NULL,
    receiver_user_name VARCHAR(256) NOT NULL,
    receiver_nick_name VARCHAR(256) NOT NULL,
    receiver_phone VARCHAR(256) NOT NULL,
    create_time BIGINT  NOT NULL
);