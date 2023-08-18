package vn.vietdefi.aoe.services.donate;

import com.google.gson.JsonObject;

public interface IDonateService {
    JsonObject donateMatch(long sender, long star, long match_id);
    JsonObject createDonateMatch(JsonObject data);
    JsonObject getDonateMatchById(long id);

    JsonObject donateGamer(long sender, long star, long gamer_id);
    JsonObject createDonateGamer(JsonObject data);
    JsonObject updateDonateGamer(JsonObject data);
    JsonObject getDonateGamerById(long id);

    JsonObject donateCaster(long sender, long star, long caster_id);
    JsonObject createDonateCaster(JsonObject data);
    JsonObject updateDonateCaster(JsonObject data);
    JsonObject getDonateCasterById(long id);

}
