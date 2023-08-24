package vn.vietdefi.aoe.services.user;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface IUserService {
    long getTotalNewUserForWeek();
    long getTotalUser();

    JsonObject statistic();
}
