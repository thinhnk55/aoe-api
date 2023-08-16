package vn.vietdefi.bank.services;

import com.google.gson.JsonObject;
import vn.vietdefi.bank.logic.BalanceTransaction;

import java.util.List;

public interface IBankService {
    JsonObject createBankAccount(JsonObject data);

    JsonObject getActiveBanks();

    void processBalanceTransaction(List<BalanceTransaction> update);
}
