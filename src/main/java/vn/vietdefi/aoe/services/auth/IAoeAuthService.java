package vn.vietdefi.aoe.services.auth;

import com.google.gson.JsonObject;

public interface IAoeAuthService {
    JsonObject register(JsonObject data);
    JsonObject login(JsonObject data);

    /*These function user for TEST only. In real situation these actions is prohibited*/
    JsonObject deleteUser(long userId);
}
