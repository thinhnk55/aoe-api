package vn.vietdefi.aoe.services.star;

import com.google.gson.JsonObject;

public interface IStarService {
    JsonObject createStarWallet(long userId);
    JsonObject getStarWallet(long userId);
    JsonObject starRechargeLog(long userId,int recordPerPage, int page);
    JsonObject starTransactionLog(long userId,int recordPerPage, int page);
    JsonObject getDetailTransaction(long id);
    JsonObject exchangeStar(int amount, int service, String username, long referId);
}
