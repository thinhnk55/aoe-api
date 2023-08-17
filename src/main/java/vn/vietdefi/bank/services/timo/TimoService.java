package vn.vietdefi.bank.services.timo;

import com.google.gson.JsonObject;

public class TimoService implements ITimoService{

    @Override
    public JsonObject login(String username, String password) {
        //Create database record
        //Login and handle state
        return null;
    }

    @Override
    public JsonObject commit(String username, String password, String otp) {
        //commit otp
        //call card name
        // update database
        return null;
    }

    @Override
    public JsonObject getAccountById(long id) {
        return null;
    }

    @Override
    public void retryLogin(JsonObject other) {

    }
}
