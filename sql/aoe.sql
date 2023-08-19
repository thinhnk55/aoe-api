CREATE TABLE IF NOT EXISTS aoe_profile  (
    user_id        BIGINT PRIMARY KEY NOT NULL,
    username       VARCHAR(128) UNIQUE NOT NULL,
    nick_name      VARCHAR(2048) UNIQUE NOT NULL,
    level          INT NOT NULL DEFAULT 0,
    avatar         VARCHAR(2048) NOT NULL,
    language_state INT DEFAULT 1
);


CREATE TABLE IF NOT EXISTS gamer
(
    user_id      BIGINT AUTO_INCREMENT       PRIMARY KEY,
    nick_name    VARCHAR(128)                 NOT NULL,
    fullname     VARCHAR(128)                 NOT NULL,
    avatar       VARCHAR(2048)                NOT NULL,
    detail_info  JSON,
    clan_id      BIGINT DEFAULT 0             NOT NULL,
    `rank`       INT    DEFAULT 0             NOT NULL,
    rank_info    TEXT                         NULL,
    match_played INT    DEFAULT 0             NOT NULL,
    match_won    INT    DEFAULT 0             NOT NULL,
    update_time  BIGINT DEFAULT 0             NOT NULL,
    status       INT                          NULL,
    phone        VARCHAR(16)                  NOT NULL,
    username     VARCHAR(128)                 NULL
);


CREATE UNIQUE INDEX gamer_nickname_unique_index ON gamer (nick_name);


CREATE TABLE IF NOT EXISTS aoe_caster
(
    user_id    BIGINT      PRIMARY KEY                 NOT NULL ,
    fullname   VARCHAR(256)                 DEFAULT '' NOT NULL,
    nick_name  VARCHAR(128)                 DEFAULT '' NOT NULL,
    avatar     TEXT                         DEFAULT '' NOT NULL,
    detail     JSON,
    phone      VARCHAR(64)                  DEFAULT '' NOT NULL,
    image      JSON                         DEFAULT '' NOT NULL,
    is_deleted INT                          DEFAULT 0  NULL,
    clan_id    BIGINT                       DEFAULT 0  NULL
);


CREATE UNIQUE INDEX caster_nickname_unique_index ON aoe_caster(nick_name);


CREATE TABLE IF NOT EXISTS aoe_clan
(
    id            BIGINT AUTO_INCREMENT      primary key,
    clan_name     VARCHAR(128) DEFAULT ''     NULL,
    avatar        TEXT         DEFAULT ''     NULL,
    create_day    BIGINT                     NOT NULL,
    founder       VARCHAR(128) DEFAULT ''     NOT NULL,
    owner_unit    VARCHAR(128) DEFAULT ''     NOT NULL,
    sport         VARCHAR(128) DEFAULT ''     NOT NULL,
    detail_info   TEXT,
    status        INT           DEFAULT 0      NOT NULL,
    clan_fullname VARCHAR(128)                 NOT NULL
);

