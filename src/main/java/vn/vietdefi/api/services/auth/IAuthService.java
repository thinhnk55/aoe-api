package vn.vietdefi.api.services.auth;

import com.google.gson.JsonObject;

public interface IAuthService {
    JsonObject register(String username,
                        String password,
                        int role,
                        int status);
    JsonObject get(long userid);
    JsonObject login(String username,
                     String password);
    JsonObject login(long userid);
    JsonObject login(String username);

    JsonObject logout(long userid);

    JsonObject authorize(long userid, String token);
    JsonObject lock(long userid);
}
