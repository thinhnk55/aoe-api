CREATE TABLE IF NOT EXISTS gamer  (
    user_id BIGINT PRIMARY KEY NOT NULL,
    nickname VARCHAR  NOT NULL,
    main_name VARCHAR  NOT NULL,
    avatar VARCHAR  ,
    detail_infoJSON,									#{"day_of_birth":"07-09-2002", "address":"NB","sport":"aoe","fb_link":"...","tiktok_link":"...","image":"img.jpg"...}
    clan_id BIGINT  ,
    rank INT  ,										#1:chuyên nghiệp, 2:bán chuyên, 3 : phong trào
    rank_info TEXT  ,									#{"team":"Top 1", "solo_ramdom":"Top 1"}
    match_played INT  NOT NULL DEFAULT 0,
    match_won INT  NOT NULL DEFAULT 0,
    update_time BIGINT  ,
    status INT  NOT NULL	                             #0 : gamer hoạt động; 1 gamer bị vô hiệu hóa
);
CREATE UNIQUE INDEX nickname_uindex ON aoe.gamer (nickname);

CREATE TABLE IF NOT EXISTS match  (
    match_id BIGINT PRIMARY KEY NOT NULL,
    star_current INT,						    		#số sao hiện tại đã nhận được từ donate
    star_default INT,			    		   	        #số sao để bắt đầu kèo
    type INT,											#1:Random / 2:R Shang / 3:R Assyrian /4:Sáng tạo
    format INT,									    #1:"1vs1";2:"2vs2"; 3: "3vs3" ; 4: "4vs4"; 5: "Quần chiến"
    time_exprired DATETIME ,	   					    # hạn donate của kèo
    detail JSON,					    				#{"discription":"Giải AOE Bán chuyên Hà NỘI...",""}
    status INT     					                #1:trận đấu chưa đủ sao, 2:trận đấu đủ số sao để bắt đầu (Chuẩn bị diễn ra) , 3 trận đấu đang diễn ra , 4 trận đấu đã kết thúc
    ) ;
CREATE TABLE IF NOT EXISTS transaction_history (
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id BIGINT  NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    amount INT  NOT NULL DEFAULT 0,									#Số sao giao dịch
    service INT  NOT NULL DEFAULT 0,							  	#1: nạp ;2: donate kèo; 3: donate:game thủ; 4 đề xuất kèo đấu ;5:hoàn trả
    balance INT NOT NULL ,									#số dư hiện tại
    create_time BIGINT  NOT NULL ,							#thời gian giao dịch
    detail TEXT  CHARACTER SET UTF16  COLLATE UTF16_BIN  NOT NULL,
    receiver_id INT  DEFAULT NULL	,						#id đối tượng nhận (match/gamer)
    status INT,
    bank_receive_id INT NOT NULL                          Id bank nhận tiền của hệ thống
    refNo VARCHAR(255) default '0'                        mã giao dịch từ ngân hàng nếu có
    );
CREATE TABLE IF NOT EXISTS aoe_profile  (
    user_id      BIGINT PRIMARY KEY NOT NULL,
    username    VARCHAR(128) UNIQUE NOT NULL,
    nick_name    VARCHAR(2048) UNIQUE NOT NULL,
    level        INT NOT NULL DEFAULT 0,
    avatar       VARCHAR(2048) NOT NULL
);

CREATE UNIQUE INDEX gamer_nickname_unique_index ON gamer (nick_name);

CREATE TABLE IF NOT EXISTS gamer  (
    user_id      BIGINT PRIMARY KEY NOT NULL,
    nick_name     VARCHAR(2048) UNIQUE NOT NULL,
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
CREATE UNIQUE INDEX gamer_nickname_unique_index ON gamer (nick_name);


CREATE TABLE IF NOT EXISTS caster (
    user_id INT(11) PRIMARY KEY NOT NULL,
    fullname VARCHAR(2048) NOT NULL DEFAULT '',
    nick_name VARCHAR(2048) UNIQUE NOT NULL DEFAULT '',
    avatar VARCHAR(2048) NOT NULL DEFAULT '',
    detail JSON ,
    phone_number VARCHAR(2048) NOT NULL DEFAULT '',
    image JSON NOT NULL DEFAULT '' ,
    is_deleted INT(11)  NULL DEFAULT 0,
    clan_id BIGINT(20) NULL DEFAULT 0
);
CREATE UNIQUE INDEX caster_nickname_unique_index ON caster (nick_name);


CREATE TABLE IF NOT EXISTS clan  (
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    clan_name VARCHAR(2048) DEFAULT '',
    avatar VARCHAR(2048) DEFAULT '',
    create_day DATETIME,
    founder VARCHAR(2048)  NOT NULL DEFAULT '',
    owner_unit VARCHAR(2048) NOT NULL DEFAULT '',
    sport VARCHAR(2048) NOT NULL DEFAULT '',
    detail_info JSON,
    status INT NOT NULL
    );