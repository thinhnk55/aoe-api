package vn.vietdefi.bank.services.timo;

import com.google.gson.JsonObject;

public interface ITimoService {
    JsonObject loginTimo(String username, String password);
    JsonObject updateToken(long timoId, String token);
    JsonObject commitTimo(long id, String otp);

    JsonObject getAccountById(long id);
    JsonObject getAccountByUsername(String username);
    JsonObject retryLogin(long id);
    void updateOther(long id, JsonObject other);
    void updateBankAccountId(long id, long bankAccountId);
    boolean isExistedByUsername(String username);
}
