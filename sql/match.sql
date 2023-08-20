CREATE TABLE IF NOT EXISTS aoe_match  (
     id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL ,
     star_current INT NOT NULL default 0,       #số sao hiện tại đã nhận được từ donate
     star_default INT NOT NULL default 0,       #số sao để bắt đầu kèo
     type INT,                                  #1:Random / 2:R Shang / 3:R Assyrian /4:Sáng tạo
     format INT,                                #1:"1vs1";2:"2vs2"; 3: "3vs3" ; 4: "4vs4"; 5: "Quần chiến"
     time_expired BIGINT,                       # hạn donate của kèo
     detail JSON,                               #{"discription":"Giải AOE Bán chuyên Hà NỘI...",""}
     state INT,                                 #1:trận đấu chưa đủ sao, 2:trận đấu đủ số sao để bắt đầu (Chuẩn bị diễn ra) , 3 trận đấu đang diễn ra , 4 trận đấu đã kết thúc
     create_time BIGINT NOT NULL,
     suggester_id BIGINT NOT NULL,
     team_player TEXT NOT NULL
);
CREATE TABLE aoe_match_suggest
(
    id                              BIGINT             PRIMARY KEY   AUTO_INCREMENT,
    sub_star_transaction_id         BIGINT             NOT NULL,
    refund_star_transaction_id      BIGINT             NOT NULL DEFAULT 0,
    suggester_id                    BIGINT             NOT NULL,
    match_id                        BIGINT             NOT NULL DEFAULT 0,
    type                            INT                NOT NULL,
    format                          INT                NOT NULL,
    team_play                       TEXT               NOT NULL,
    detail                          TEXT               NOT NULL,
    amount                          INT                NOT NULL,
    create_time                     BIGINT             NOT NULL,
    state                           INT                NOT NULL DEFAULT 0
);

