package vn.vietdefi.aoe.services.profile;

import com.google.gson.JsonObject;

public interface IProfileService {
    JsonObject getUserProfileByUserId(long userId);
    JsonObject updateUserProfile(JsonObject data);
}
