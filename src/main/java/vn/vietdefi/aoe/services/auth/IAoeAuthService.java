package vn.vietdefi.aoe.services.auth;

import com.google.gson.JsonObject;

public interface IAoeAuthService {
    JsonObject register(JsonObject data);
    JsonObject login(JsonObject data);
}
