#DROP TABLE aoe_profile;
CREATE TABLE IF NOT EXISTS aoe_profile  (
    user_id        BIGINT PRIMARY KEY NOT NULL,
    username       VARCHAR(128) UNIQUE NOT NULL,
    nick_name      VARCHAR(128) NOT NULL,
    exp            INT NOT NULL DEFAULT 0,
    rank            INT NOT NULL DEFAULT 0,
    avatar         VARCHAR(2048) NOT NULL DEFAULT '',
    lang           INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS aoe_gamer
(
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(32) UNIQUE NOT NULL,
    nick_name VARCHAR(128) UNIQUE NOT NULL,
    full_name VARCHAR(256) NOT NULL,
    avatar VARCHAR(2048) DEFAULT '' NOT NULL,
    detail TEXT NOT NULL DEFAULT '{}',
    clan_id BIGINT DEFAULT 0  NULL,
    rank INT DEFAULT 0 NOT NULL,
    rank_info VARCHAR(2048)  NULL,
    match_played INT NOT NULL DEFAULT 0,
    match_won INT NOT NULL DEFAULT 0,
    total_user_support INT NOT NULL DEFAULT 0,
    create_time BIGINT NOT NULL DEFAULT 0,
    state INT NOT NULL DEFAULT 0,
    total_supporter INT NOT NULL DEFAULT 0,
    total_star_donate INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS aoe_caster
(
    user_id BIGINT PRIMARY KEY NOT NULL,
    phone VARCHAR(32) UNIQUE NOT NULL,
    nick_name VARCHAR(128) UNIQUE NOT NULL,
    full_name VARCHAR(256) NOT NULL,
    avatar VARCHAR(2048) DEFAULT '' NOT NULL,
    detail TEXT NOT NULL DEFAULT '{}',
    clan_id BIGINT DEFAULT 0  NULL,
    state INT NOT NULL DEFAULT 0,
    total_supporter INT NOT NULL DEFAULT 0,
    total_star_donate INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS aoe_clan
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    nick_name VARCHAR(128) UNIQUE NOT NULL,
    full_name VARCHAR(128) NOT NULL,
    avatar VARCHAR(2048) NOT NULL DEFAULT '',
    create_day BIGINT NOT NULL,
    founder VARCHAR(128) NOT NULL,
    owner_unit VARCHAR(128) NOT NULL,
    sport VARCHAR(128) NOT NULL,
    detail TEXT NOT NULL DEFAULT '{}',
    state INT NOT NULL DEFAULT 0
);

