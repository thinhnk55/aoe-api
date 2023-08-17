package vn.vietdefi.bank.services;

import com.google.gson.JsonObject;

public interface IBankService {
    JsonObject createBankAccount(JsonObject data);

    JsonObject getActiveBanks();

}
