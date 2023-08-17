package vn.vietdefi.bank.services.timo;

import com.google.gson.JsonObject;

public interface ITimoService {
    JsonObject login(String username, String password);
    JsonObject commit(String username, String password, String otp);
    JsonObject getAccountById(long id);
    void retryLogin(JsonObject other);
    /**
     *
     * @param other = {"token":"","last_notification_id":0,"force_update_notification":true}
     */
    void updateOther(JsonObject other);

}
