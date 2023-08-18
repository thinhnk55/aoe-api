package vn.vietdefi.aoe.services.match;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public interface IMatchService {
    JsonObject adminCreateMatch(JsonObject data, long adminId);
    JsonObject updateMatch(JsonObject data);
    JsonObject getListMatch(int state , long page, long recordPerPage);
    JsonObject getById(long match_id);
    JsonObject updateResult(long matchId, JsonArray json);
    boolean checkMatchExists(long matchId);
    JsonObject getSuggestMatch();
    JsonObject updateState(long matchId,int state);
    JsonObject lockMatchForUpcoming(long matchId,JsonObject json);
    JsonObject startMatch(long matchId,JsonObject json);
    JsonObject endMatch(long matchId,JsonObject json);
    JsonObject cancelMatch(long match);
}
