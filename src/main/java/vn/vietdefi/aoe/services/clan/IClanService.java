package vn.vietdefi.aoe.services.clan;

import com.google.gson.JsonObject;



public interface IClanService {
    JsonObject createClan(JsonObject json);
    JsonObject updateClan(JsonObject data);
    JsonObject getClanById(long clanId);
    JsonObject getListClan(long page, long recordPerPage);
}
