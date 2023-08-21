package vn.vietdefi.aoe.services.clan;

import com.google.gson.JsonObject;



public interface IClanService {
    JsonObject createClan(JsonObject json);
    JsonObject updateClan(JsonObject data);
    JsonObject getClanById(long clanId);
    JsonObject getClanByNickName(String nickName);
    JsonObject getListClan(long page, long recordPerPage);
    /*These function user for TEST only. In real situation these actions is prohibited*/
    JsonObject deleteClan(long clanId);
}
