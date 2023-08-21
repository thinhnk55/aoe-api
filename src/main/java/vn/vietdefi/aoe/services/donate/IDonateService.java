package vn.vietdefi.aoe.services.donate;

import com.google.gson.JsonObject;

public interface IDonateService {
    JsonObject donate(long sender, long star, int service, long target_id, String message);
    void updateDonateUsed(long id, long add_star_transaction_id);
    public JsonObject createDonate(JsonObject data);
    public JsonObject getDonateById(long id);
    JsonObject listDonateByTargetId(long targetId, long page, long recordPerPage);
    JsonObject listAllTopDonate(long time, long page, long recordPerPage);
    JsonObject listTopDonateByTargetId(long time, long targetId, long page, long recordPerPage);
    long getTotalDonateByUserId(long id);
}
