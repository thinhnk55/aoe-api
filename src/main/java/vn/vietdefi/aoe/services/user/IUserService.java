package vn.vietdefi.aoe.services.user;

import com.google.gson.JsonObject;

public interface IUserService {
    boolean isExistPhone(String phoneNumber);

    JsonObject lockUser(long userId);

    JsonObject unLockUser(long userId);

    JsonObject forgotPassword(JsonObject data);
}
