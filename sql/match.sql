CREATE TABLE IF NOT EXISTS aoe_match  (
     match_id BIGINT PRIMARY KEY NOT NULL,
     star_current INT NOT NULL default 0,       #số sao hiện tại đã nhận được từ donate
     star_default INT NOT NULL default 0,       #số sao để bắt đầu kèo
     type INT,               #1:Random / 2:R Shang / 3:R Assyrian /4:Sáng tạo
     format INT,             #1:"1vs1";2:"2vs2"; 3: "3vs3" ; 4: "4vs4"; 5: "Quần chiến"
     time_expired BIGINT,    # hạn donate của kèo
     detail JSON,            #{"discription":"Giải AOE Bán chuyên Hà NỘI...",""}
     state INT,               #1:trận đấu chưa đủ sao, 2:trận đấu đủ số sao để bắt đầu (Chuẩn bị diễn ra) , 3 trận đấu đang diễn ra , 4 trận đấu đã kết thúc
     create_time BIGINT NOT NULL,
     suggester_id BIGINT NOT NULL,
     team_player TEXT NOT NULL,
     caster_id BIGINT NOT NULL default 0
) ;
create table aoe_match_suggest
(
    id           BIGINT        PRIMARY KEY   AUTO_INCREMENT   ,
    suggester_id BIGINT        NOT NULL,
    type         INT           NOT NULL,
    format       INT           NOT NULL,
    team_play    TEXT          NOT NULL,
    detail       TEXT          NOT NULL,
    star         INT           NULL,
    create_time  BIGINT        NOT NULL,
    state        INT           NOT NULL default 0
);

