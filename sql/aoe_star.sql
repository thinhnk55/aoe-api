CREATE TABLE IF NOT EXISTS aoe_star (
     user_id BIGINT  PRIMARY KEY NOT NULL,
     username VARCHAR NOT NULL,
     balance INT  NOT NULL DEFAULT 0
) ;

CREATE TABLE IF NOT EXISTS aoe_star_transaction (
    id BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    user_id BIGINT  NOT NULL,
    username VARCHAR(255) NOT NULL,
    service INT  NOT NULL,							  	    #1: nạp ;2: donate kèo; 3: donate:game thủ; 4 đề xuất kèo đấu ; 5:hoàn trả
    refer_id BIGINT NOT NULL DEFAULT 0,
    amount INT  NOT NULL,									#Số sao giao dịch
    balance INT NOT NULL,									#số dư hiện tại
    create_time BIGINT  NOT NULL							#thời gian giao dịch
);


