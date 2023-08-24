package vn.vietdefi.aoe.services.user;

import com.google.gson.JsonElement;

public interface IUserService {
    long getTotalNewUserForWeek();
    long getTotalUser();

    JsonElement statistic();
}
