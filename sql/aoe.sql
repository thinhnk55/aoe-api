CREATE TABLE IF NOT EXISTS gamer  (
    user_id      BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    nickname     VARCHAR(2048) UNIQUE NOT NULL,
    main_name    VARCHAR(2048) NOT NULL,
    avatar       VARCHAR(2048) NOT NULL ,
    detail_info  JSON,									#{"day_of_birth":"07-09-2002", "address":"NB","sport":"aoe","fb_link":"...","tiktok_link":"...","image":"img.jpg"..}
    clan_id      BIGINT NOT NULL DEFAULT 0,
    rank         INT NOT NULL DEFAULT 0 ,				#1:chuyên nghiệp, 2:bán chuyên, 3 : phong trào
    rank_info    TEXT  ,							    #{"team":"Top 1", "solo_ramdom":"Top 1"}
    match_played INT NOT NULL DEFAULT 0,
    match_won    INT NOT NULL DEFAULT 0,
    update_time  BIGINT NOT NULL DEFAULT 0,
    status       INT,                                   #0 : gamer hoạt động; 1 gamer bị vô hiệu hóa
    phone_number VARCHAR(2048) NOT NULL
);
CREATE UNIQUE INDEX gamer_nickname_unique_index ON gamer (nickname);


CREATE TABLE IF NOT EXISTS caster (
    id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
    fullname VARCHAR(2048) NOT NULL DEFAULT '',
    nickname VARCHAR(2048) UNIQUE NOT NULL DEFAULT '',
    avatar VARCHAR(2048) NOT NULL DEFAULT '',
    detail JSON ,
    phone_number VARCHAR(2048) NOT NULL DEFAULT '',
    image JSON NOT NULL DEFAULT '' ,
    is_deleted INT(11)  NULL DEFAULT 0,
    clan_id BIGINT(20) NULL DEFAULT 0
);
CREATE UNIQUE INDEX caster_nickname_unique_index ON caster (nickname);
