package vn.vietdefi.aoe.services.donate;

import com.google.gson.JsonObject;

public interface IDonateService {
    JsonObject donate(long sender, long star, int service, long target_id, String message);
    void updateDonateUsed(long id, long add_star_transaction_id);
    public JsonObject createDonate(JsonObject data);
    public JsonObject getDonateById(long id);
    JsonObject listDonateByTargetId(int service, long targetId, long page, long recordPerPage);
    JsonObject listAllTopDonate(long page, long recordPerPage);
    JsonObject listTopDonateByTargetId(int service, long targetId, long page, long recordPerPage);
    JsonObject statisticDonateByTargetId(int service, long target_id);
    JsonObject statisticTotalDonate(long from, long to);
    JsonObject listDonateOfUser(long userId, long page, long recordPerPage);
    JsonObject listGamerFavorites(long userId, long page, long recordPerPage);
    JsonObject filterListDonate(String phoneNumber, long from, long to, int service, long page, long recordPerPage);
    JsonObject filterStatisticDonate(int service, long targetId, long page, long recordPerPage);
    JsonObject listDonateOutstanding();
    JsonObject refundStarDonate(long matchId);
    JsonObject statisticDonateByUserId();



    /*These function user for TEST only. In real situation these actions is prohibited*/
    JsonObject deleteDonateBySenderId(long userId);


    JsonObject getDetailDonateById(long id);
}
