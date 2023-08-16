package vn.vietdefi.bank.services;

import com.google.gson.JsonObject;
import vn.vietdefi.bank.logic.BalanceTransaction;

import java.util.List;

public class BankService implements IBankService{
    @Override
    public JsonObject createBankAccount(JsonObject data) {
        return null;
    }

    @Override
    public JsonObject getActiveBanks() {
        return null;
    }

    @Override
    public void processBalanceTransaction(List<BalanceTransaction> update) {

    }
}
