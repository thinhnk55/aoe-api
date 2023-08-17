package vn.vietdefi.aoe.services.user;

import com.google.gson.JsonObject;

public interface IUserService {
    JsonObject getUserProfile(long userId);
}
