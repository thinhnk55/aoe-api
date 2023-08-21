package vn.vietdefi.aoe.services.user.gamer;

import com.google.gson.JsonObject;

public interface IGamerService {
    JsonObject create(JsonObject json);
    JsonObject getGamerByUserId(long user_id);
    JsonObject update(JsonObject json);
    JsonObject listGamer(long page, long recordPerPage);
    JsonObject listGamerOfClan(long clanId, long page, long recordPerPage);
    JsonObject deleteGamerById(long id);
}
