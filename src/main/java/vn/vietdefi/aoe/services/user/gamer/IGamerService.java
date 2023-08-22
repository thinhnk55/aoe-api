package vn.vietdefi.aoe.services.user.gamer;

import com.google.gson.JsonObject;

public interface IGamerService {
    JsonObject create(JsonObject json);
    JsonObject getGamerByUserId(long userId);
    JsonObject update(JsonObject json);
    JsonObject listGamer(long page, long recordPerPage);
    JsonObject listGamerByClanId(long clanId, long page, long recordPerPage);
    JsonObject deleteGamerByUserId(long userId);
}
