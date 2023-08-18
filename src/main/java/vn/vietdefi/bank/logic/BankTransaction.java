package vn.vietdefi.bank.logic;

import com.google.gson.JsonObject;

/**
 * CREATE TABLE IF NOT EXISTS bank_transaction
 * (
 * id                    BIGINT       PRIMARY KEY AUTO_INCREMENT,
 * bank_transaction_id   VARCHAR(50)              NOT NULL,
 * receiver_bankcode     INT          DEFAULT 0   NOT NULL,
 * receiver_bank_account VARCHAR(50)  DEFAULT ''  NOT NULL,
 * receiver_bank_name    VARCHAR(200) DEFAULT ''  NOT NULL,
 * receiver_name         VARCHAR(50)  DEFAULT ''  NOT NULL,
 * note                  TEXT         DEFAULT ''  NOT NULL,
 * amount                BIGINT       DEFAULT 0   NOT NULL,
 * sender_bankcode       INT          DEFAULT 0   NOT NULL,
 * sender_bank_account   VARCHAR(50)  DEFAULT ''  NOT NULL,
 * sender_bank_name      VARCHAR(252) DEFAULT ''  NOT NULL,
 * sender_name           VARCHAR(252) DEFAULT ''  NOT NULL,
 * create_time           BIGINT       DEFAULT 0   NOT NULL,
 * status                INT          DEFAULT 0   NOT NULL,
 * star_transaction_id   BIGINT       DEFAULT -1  NOT NULL
 * );
 */
public class BankTransaction {
    public long id;
    public String bank_transaction_id;
    public int receiver_bankcode;
    public String receiver_bank_account;
    public String receiver_name;
    public String note;
    public long amount;
    public int sender_bankcode;
    public String sender_bank_account;
    public String sender_name;
    public long create_time;
    public int state;
    public long star_transaction_id;

    public BankTransaction(JsonObject data) {
        id = data.get("id").getAsLong();
        bank_transaction_id = data.get("bank_transaction_id").getAsString();
        receiver_bankcode = data.get("receiver_bankcode").getAsInt();
        receiver_bank_account = data.get("receiver_bank_account").getAsString();
        receiver_name = data.get("receiver_name").getAsString();
        note = data.get("note").getAsString();
        amount = data.get("amount").getAsLong();
        sender_bankcode = data.get("sender_bankcode").getAsInt();
        sender_bank_account = data.get("sender_bank_account").getAsString();
        sender_name = data.get("sender_name").getAsString();
        create_time = data.get("create_time").getAsLong();
        state = data.get("state").getAsInt();
        star_transaction_id = data.get("star_transaction_id").getAsLong();
    }
}
