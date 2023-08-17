package vn.vietdefi.bank.services.timo;

import com.google.gson.JsonObject;

public interface ITimoService {
    JsonObject login(String username, String password);
    JsonObject commit(String username, String password, String otp);

    JsonObject getAccountById(long id);

    void retry(JsonObject other);
}
