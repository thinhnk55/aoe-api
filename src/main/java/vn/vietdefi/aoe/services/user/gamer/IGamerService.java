package vn.vietdefi.aoe.services.user.gamer;

import com.google.gson.JsonObject;

public interface IGamerService {
    JsonObject create(JsonObject json);
    JsonObject getGamerByUserId(long user_id);
    JsonObject updateInfo(JsonObject json);
    JsonObject listGamerByMatchId(long matchId);

    JsonObject listGamer(long page, long recordPerPage);

    JsonObject listGamerOfClan(long id, long page, long recordPerPage);

    JsonObject listMatch(long id, long page, long recordPerPage);
}
