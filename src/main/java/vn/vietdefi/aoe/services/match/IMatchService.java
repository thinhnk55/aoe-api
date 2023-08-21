package vn.vietdefi.aoe.services.match;

import com.google.gson.JsonObject;

public interface IMatchService {
    JsonObject createMatch(JsonObject data);
    JsonObject updateMatch(JsonObject data);
    JsonObject getListMatch(int state , long page, long recordPerPage);
    JsonObject getById(long match_id);
    JsonObject updateResult(JsonObject json);
    boolean checkMatchExists(long matchId);
    JsonObject getOutstandingMatch();
    JsonObject updateState(long matchId,int state);
    JsonObject lockMatchForUpcoming(JsonObject json);
    JsonObject startMatch(JsonObject json);
    JsonObject endMatch(JsonObject json);
    JsonObject cancelMatch(long match);

    JsonObject addStarCurrentMatch(long matchId, long amount);
    long totalMatch(long userId);
    JsonObject checkAcceptDonate(long targetId);
}
