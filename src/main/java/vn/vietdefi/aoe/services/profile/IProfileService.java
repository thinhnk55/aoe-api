package vn.vietdefi.aoe.services.profile;

import com.google.gson.JsonObject;

public interface IProfileService {
    JsonObject createUserProfile(long userId);
    JsonObject getUserProfile(long userId);
    JsonObject updateUserProfile(JsonObject data);
}
