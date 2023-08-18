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
    JsonObject getOutstandingMatch();
    JsonObject updateState(long matchId,int state);
    JsonObject lockMatchForUpcoming(JsonObject json);
    JsonObject startMatch(JsonObject json);
    JsonObject endMatch(JsonObject json);
    JsonObject cancelMatch(long match);
    JsonObject createMatchSuggest(JsonObject data ,long userId);
    JsonObject updateMatchSuggest( long matchSuggestId,JsonObject data);
    JsonObject getListMatchSuggested(long userId,long page, long recordPerPage);
    JsonObject updateStarCurrentMatch(long matchId, long amount);


}
