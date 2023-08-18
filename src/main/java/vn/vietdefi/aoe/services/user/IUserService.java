package vn.vietdefi.aoe.services.user;

import com.google.gson.JsonObject;

public interface IUserService {
    JsonObject createUserProfile(long userId);
    JsonObject getUserProfile(long userId);
    JsonObject updateUserProfile(JsonObject data);
}
