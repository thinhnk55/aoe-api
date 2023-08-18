package vn.vietdefi.aoe.services.donate;

import com.google.gson.JsonObject;

public interface IDonateService {
    JsonObject createDonateMatch(JsonObject data);
    JsonObject getDonateMatchById(long id);
    JsonObject createDonateGamer(JsonObject data);
    JsonObject updateDonateGamer(JsonObject data);
    JsonObject getDonateGamerById(long id);
}
