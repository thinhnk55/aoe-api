package vn.vietdefi.aoe.services.profile;

import com.google.gson.JsonObject;

public interface IProfileService {
    JsonObject getUserProfileByUserId(long userId);
    JsonObject updateUserProfile(long userId, JsonObject data);
    JsonObject searchProfile(String username);
    JsonObject updateLanguage(long id, int state);
    JsonObject updateNickName(long id, String nickname);
    /*These function user for TEST only. In real situation these actions is prohibited*/
    JsonObject deleteProfile(long userId);

    JsonObject getOutstandingView(long userId, long page, int recordPerPage);

    JsonObject getListGamerFavorites(long userId, long page, int recordPerPage);

    JsonObject getPartialProfile(long userId);
}
