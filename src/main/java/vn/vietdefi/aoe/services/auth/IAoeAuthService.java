package vn.vietdefi.aoe.services.auth;

import com.google.gson.JsonObject;

public interface IAoeAuthService {
    JsonObject register(String username,
                        String password,
                        int role,
                        int status);
    JsonObject register(JsonObject data);

    JsonObject login(JsonObject data);
    JsonObject changeStatus(long userId, int status);
    JsonObject get(String username);

    /*These function user for TEST only. In real situation these actions is prohibited*/
    JsonObject deleteUser(long userId);

    JsonObject setPasswordByUsername(JsonObject json);
}
