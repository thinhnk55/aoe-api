package vn.vietdefi.aoe.services.clan;

import com.google.gson.JsonObject;



public interface IClanService {
    JsonObject createClan(JsonObject json);
    JsonObject updateClan(JsonObject data);
    JsonObject getClanInfo(long clanId);
    JsonObject getListClan(long page, long recordPerPage);
}
