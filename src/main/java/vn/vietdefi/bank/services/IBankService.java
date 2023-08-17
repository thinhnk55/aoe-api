package vn.vietdefi.bank.services;

import com.google.gson.JsonObject;

public interface IBankService {
    JsonObject createBankAccount(int bankCode, String accountOwner, String accountNumber, long bankDetailId);
    JsonObject getWorkingBanks();
    void updateBankState(long id, int state);
    JsonObject createBalanceTransaction(JsonObject data);
    JsonObject createBankAccountFromTimoAccount(JsonObject data);
    JsonObject getBankAccount(int bankCode, String accountNumber);
    JsonObject getAccountById(long id);

}
