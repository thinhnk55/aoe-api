package vn.vietdefi.bank.services;

import com.google.gson.JsonObject;

public interface IBankService {
    //Bank Account
    JsonObject createBankAccount(int bankCode, String accountOwner, String accountNumber, long bankDetailId);
    JsonObject getWorkingBankAccount();
    JsonObject getOneWorkingBankAccount();
    JsonObject updateBankAccountState(long id, int state);

    JsonObject getBankAccountByBankCodeAndAccountNumber(int bankCode, String accountNumber);
    JsonObject getAccountById(long id);

    JsonObject listBankAccountByState(int state, long page, long recordPerPage);

    JsonObject waitToWork(long id);
    JsonObject startWorking(long id);
    JsonObject disable(long id);

    JsonObject login(int bankCode, String username, String password);
    JsonObject commitOTP(int bankCode, long id, String otp);
}
