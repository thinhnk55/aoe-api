package vn.vietdefi.aoe.services.clan;

import com.google.gson.JsonObject;



public interface IClanService {
    JsonObject createClan(JsonObject json);
    JsonObject updateClan(long clanId,JsonObject json);
    JsonObject getInfoClan(long clanId);
    JsonObject getClan(long id);
    JsonObject getListClan();
}
