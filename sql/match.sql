CREATE TABLE IF NOT EXISTS aoe_match  (
     id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
     admin_id BIGINT NOT NULL DEFAULT 0,
     star_current INT NOT NULL default 0,
     star_default INT NOT NULL default 0,
     type INT,
     format INT,
     time_expired BIGINT,
     detail VARCHAR(4096) NOT NULL DEFAULT '{}',
     state INT,
     create_time BIGINT NOT NULL,
     suggester_id BIGINT NOT NULL,
     team_player TEXT NOT NULL DEFAULT '{}',
     action_log VARCHAR(4096) NOT NULL DEFAULT '{}'
);

CREATE TABLE IF NOT EXISTS aoe_match_suggest
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    suggester_id BIGINT NOT NULL,
    match_id BIGINT NOT NULL DEFAULT 0,
    type INT NOT NULL,
    format INT NOT NULL,
    team_play VARCHAR(256) NOT NULL DEFAULT '{}',
    detail VARCHAR(4096) NOT NULL DEFAULT '{}',
    amount INT NOT NULL,
    create_time BIGINT NOT NULL,
    state INT NOT NULL DEFAULT 0,
    action_log VARCHAR(4096) NOT NULL DEFAULT '{}'
);

CREATE TABLE IF NOT EXISTS aoe_match_gamer (
     id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
     match_id BIGINT NOT NULL,
     team INT NOT NULL,
     result INT NOT NULL,
     gamer_id INT NOT NULL,
     gamer_nick_name VARCHAR(128),
     avatar VARCHAR(2048)
);

