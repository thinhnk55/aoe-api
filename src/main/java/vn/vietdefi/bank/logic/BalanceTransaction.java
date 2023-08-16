package vn.vietdefi.bank.logic;

import com.google.gson.JsonObject;

public class BalanceTransaction {
    public int error;
    private String transactionId;
    private int receiverBankCode;
    private String receiverBankName;
    private String receiverAccount;
    private String receiverName;

    private int senderBankCode;
    private String senderBankName;
    private String senderAccount;

    private String senderName;
    private String note;
    private long exchange;
    private long balance;
    private long timestamp;

    public BalanceTransaction(JsonObject json) {
        this.error = json.get("error").getAsInt();
        this.transactionId = json.get("refNo").getAsString();
        //receiver
        this.receiverBankCode = json.get("receiver_bankcode").getAsInt();
        this.receiverBankName = json.get("receiver_bank_name").getAsString();
        this.receiverAccount = json.get("receiver_bank_account").getAsString();;
        this.receiverName = json.get("receiver_name").getAsString();
        //sender
        this.senderBankCode = json.get("sender_bankcode").getAsInt();
        this.senderBankName = json.get("sender_bank_name").getAsString();
        this.senderAccount = json.get("sender_bank_account").getAsString();
        this.senderName = json.get("sender_name").getAsString();
        this.note = json.get("note").getAsString();
        this.exchange = json.get("amount").getAsLong();
        this.balance = json.get("balance").getAsLong();
        this.timestamp = json.get("create_time").getAsLong();
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getReceiverBankCode() {
        return receiverBankCode;
    }

    public void setReceiverBankCode(int receiverBankCode) {
        this.receiverBankCode = receiverBankCode;
    }

    public String getReceiverBankName() {
        return receiverBankName;
    }

    public void setReceiverBankName(String receiverBankName) {
        this.receiverBankName = receiverBankName;
    }

    public String getReceiverAccount() {
        return receiverAccount;
    }

    public void setReceiverAccount(String receiverAccount) {
        this.receiverAccount = receiverAccount;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public int getSenderBankCode() {
        return senderBankCode;
    }

    public void setSenderBankCode(int senderBankCode) {
        this.senderBankCode = senderBankCode;
    }

    public String getSenderBankName() {
        return senderBankName;
    }

    public void setSenderBankName(String senderBankName) {
        this.senderBankName = senderBankName;
    }

    public String getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(String senderAccount) {
        this.senderAccount = senderAccount;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getExchange() {
        return exchange;
    }

    public void setExchange(long exchange) {
        this.exchange = exchange;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public BalanceTransaction(int error) {
    }
}
