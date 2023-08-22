package vn.vietdefi.api.services.auth;

import com.google.gson.JsonObject;

public interface IAuthService {
    JsonObject register(String username,
                        String password,
                        int role,
                        int status);
    JsonObject get(long userid);
    JsonObject get(String username);
    JsonObject login(String username,
                     String password);
    JsonObject login(long userid);
    JsonObject login(String username);

    JsonObject logout(long userid);

    JsonObject authorize(long userid, String token);
    JsonObject lock(long userid);
    JsonObject changeRole(long userId, int role);
    JsonObject changeRole(String username, int role);
    JsonObject changeStatus(long userId, int status);
    JsonObject changeStatus(String username, int status);
    JsonObject changePassword(long userId, String password, String newPassword);
    long getUserIdByUserName(String username);

    /*These function user for TEST only. In real situation these actions is prohibited*/
    JsonObject delete(long userid);
    JsonObject updateUserId(long userid, long newUserId);
    JsonObject updateUsername(long userid, String username);
    JsonObject updatePassword(long userid, String password);
}
