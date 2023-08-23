package vn.vietdefi.aoe.services.donate;

import com.google.gson.JsonObject;

public interface IDonateService {
    JsonObject donate(long sender, long star, int service, long target_id, String message);
    void updateDonateUsed(long id, long add_star_transaction_id);
    public JsonObject createDonate(JsonObject data);
    public JsonObject getDonateById(long id);
    JsonObject listDonateByTargetId(int service, long targetId, long page, long recordPerPage);
    JsonObject listAllTopDonate(long from, long to, long page, long recordPerPage);
    JsonObject listTopDonateByTargetId(long targetId, long from, long to, long page, long recordPerPage);
    long getTotalDonateByUserId(long id);

    /*These function user for TEST only. In real situation these actions is prohibited*/
    JsonObject deleteDonateBySenderId(long userId);

    JsonObject statisticDonate(int service, long target_id);
}
