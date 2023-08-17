package vn.vietdefi.aoe.services.star;

import com.google.gson.JsonObject;

public interface IStarService {
    JsonObject createStarWallet(long userId);
    JsonObject getStarWallet(long userId);
    JsonObject starRechargeLog(long userId, int page);
    JsonObject starTransactionLog(long userId, int page);
    JsonObject addStar(JsonObject data);
    JsonObject subStar(JsonObject data);
    JsonObject getDetailTransaction(long id);
}
