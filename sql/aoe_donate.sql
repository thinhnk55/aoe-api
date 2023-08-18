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
    user_id BIGINT  NOT NULL,
    user_name VARCHAR(256) NOT NULL,
    nick_name VARCHAR(256) NOT NULL,
    phone_number VARCHAR(256) NOT NULL,
    amount INT  NOT NULL,
    gamer_id BIGINT NOT NULL,
    message VARCHAR(256) NOT NULL,
    create_time BIGINT  NOT NULL
);