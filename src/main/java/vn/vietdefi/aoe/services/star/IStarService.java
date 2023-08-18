package vn.vietdefi.aoe.services.star;

import com.google.gson.JsonObject;

public interface IStarService {
    JsonObject getStarWalletByUserId(long userId);
    JsonObject listStarTransactionOfUserByService(long userId, int service, long page, long recordPerPage);

    JsonObject listStarTransactionOfUser(long userId, long page, long recordPerPage);
    JsonObject getStarTransactionById(long id);
    JsonObject exchangeStar(long amount, int service, String username, long referId);

    void updateReferId(long id, long referId);
    boolean checkStar(long amount, long userid);
}
