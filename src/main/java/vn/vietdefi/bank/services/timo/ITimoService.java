package vn.vietdefi.bank.services.timo;

import com.google.gson.JsonObject;

public interface ITimoService {
    JsonObject loginTimo(String username, String password);
    JsonObject commitTimo(String token, String refNo, String otp);

    JsonObject getAccountById(long id);

    void retryLogin(JsonObject other);
}
