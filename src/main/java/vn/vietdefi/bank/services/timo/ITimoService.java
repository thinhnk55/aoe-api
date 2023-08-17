package vn.vietdefi.bank.services.timo;

import com.google.gson.JsonObject;

public interface ITimoService {
    JsonObject loginTimo(String username, String password);
    JsonObject updateToken(long timoId, String token);
    JsonObject commitTimo(long id, String otp);

    JsonObject getAccountById(long id);
    JsonObject getAccountByUsername(String username);
    void retryLogin(JsonObject other);
    /**
     *
     * @param other = {"token":"","last_notification_id":0,"force_update_notification":true}
     */
    void updateOther(JsonObject other);


    void getMissNotification(JsonObject nextAccount);

    void updateTokenBank(JsonObject data, int id);

    JsonObject getInfoLogin(long id);
}
