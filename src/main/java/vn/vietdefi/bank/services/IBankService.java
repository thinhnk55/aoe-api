package vn.vietdefi.bank.services;

import com.google.gson.JsonObject;

public interface IBankService {
    JsonObject login(JsonObject data);
    JsonObject commit(JsonObject data);
    JsonObject createBankAccount(JsonObject data);

    JsonObject getActiveBanks();
    public JsonObject updateBankState(long id, int state);

}
