package vn.vietdefi.bank.services;

import com.google.gson.JsonObject;

public interface IBankTransactionService {
    //Bank Transaction
    JsonObject createBalanceTransaction(JsonObject data);

    JsonObject getBalanceTransactionById(long referId);

    JsonObject listWaitingTransaction();
    void updateBankTransactionState(long id,
                                    int state);
    void setStarTransactionId(long id, long star_transaction_id);
    void completeBankTransaction(long id,
                                 int service, long targetId);

    JsonObject fixTransaction(long id, String note);

    JsonObject listBankTransactionError();
}
