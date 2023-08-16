package vn.vietdefi.bank.logic;

import com.google.gson.JsonObject;

public class BankAccount {
    public int id;
    public int bankCode;
    public String bankName;
    public String accountNumber;
    public String accountOwner;
    public int state;
    public JsonObject other;

    public BankAccount(JsonObject json) {
        this.bankCode = json.get("bank_code").getAsInt();
        this.bankName = json.get("bank_name").getAsString();
        this.accountNumber = json.get("account_number").getAsString();
        this.accountOwner = json.get("owner").getAsString();
        this.state = json.get("state").getAsInt();
        this.other = json.getAsJsonObject("other");
    }

    public BankAccount(String accountNumber, String accountOwner, int state, JsonObject other) {
        this.bankCode = 0;
        this.bankName = "";
        this.accountNumber = accountNumber;
        this.accountOwner = accountOwner;
        this.state = state;
        this.other = other;
    }
}
