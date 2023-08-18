package vn.vietdefi.bank.services;

import com.google.gson.JsonObject;
import vn.vietdefi.bank.logic.BankTransaction;

public interface IBankService {
    //Bank Account
    JsonObject createBankAccount(int bankCode, String accountOwner, String accountNumber, long bankDetailId);
    JsonObject getWorkingBankAccount();
    void updateBankAccountState(long id, int state);

    JsonObject createBankAccountFromTimoAccount(JsonObject data);
    JsonObject getBankAccount(int bankCode, String accountNumber);
    JsonObject getAccountById(long id);

    JsonObject listBankAccount(long page, long recordPerPage);

    JsonObject addBankAccount(JsonObject data);

    JsonObject updateBankAccount(JsonObject data);

    JsonObject selectBankAccount(long id);

    //Bank Transaction
    JsonObject createBalanceTransaction(JsonObject data);

    JsonObject getBalanceTransactionById(long referId);

    JsonObject listWaitingTransaction();
    void updateBankTransactionState(long id,
                                    int state);
    void updateStarTransactionId(long id,long star_transaction_id);
    void doneBankTransactionState(long id,
                                 int service, long targetId);
}
