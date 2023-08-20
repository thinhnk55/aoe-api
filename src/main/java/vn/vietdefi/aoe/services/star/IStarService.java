package vn.vietdefi.aoe.services.star;

import com.google.gson.JsonObject;

public interface IStarService {
    JsonObject getStarWalletByUserId(long userId);
    JsonObject listStarTransactionOfUserByService(long userId, int service, long page, long recordPerPage);
    JsonObject listStarTransactionOfUser(long userId, long page, long recordPerPage);
    JsonObject listStarTransactionOfUserByTime(long time, long userId, long page, long recordPerPage);
    JsonObject getStarTransactionById(long id);
    JsonObject exchangeStar(String username, int service, long amount, long referId);
    JsonObject exchangeStar(long userId, int service, long amount, long referId);
    void updateReferId(long id, long referId);
    boolean checkStar(long amount, long userid);
}
