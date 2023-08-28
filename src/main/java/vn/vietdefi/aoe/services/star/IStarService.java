package vn.vietdefi.aoe.services.star;

import com.google.gson.JsonObject;

public interface IStarService {
    JsonObject getStarWalletByUserId(long userId);
    JsonObject listStarTransactionOfUserByService(long userId, int service, long page, long recordPerPage);
    JsonObject listStarTransactionOfUserAll(long userId, long page, long recordPerPage);
    JsonObject listStarTransactionOfUserByTime(long userId, long from, long to, long page, long recordPerPage);
    JsonObject getStarTransactionById(long id);
    JsonObject exchangeStar(String username, int service, long amount, long referId);
    JsonObject exchangeStar(long userId, int service, long amount, long referId);
    JsonObject lookupRechargeHistory(String phoneNumber, long from, long to ,long page, long recordPerPage);
    void updateReferId(long id, long referId);
    boolean checkStar(long amount, long userid);

    /*These function user for TEST only. In real situation these actions is prohibited*/
    JsonObject deleteStarWallet(long userId);
}
