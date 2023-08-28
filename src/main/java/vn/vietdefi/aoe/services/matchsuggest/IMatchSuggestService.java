package vn.vietdefi.aoe.services.matchsuggest;

import com.google.gson.JsonObject;

public interface IMatchSuggestService {
    JsonObject createMatchSuggest(long suggester, JsonObject data);
    JsonObject updateMatchSuggest( long id, JsonObject data);
    JsonObject getMatchSuggest(long matchId);
    JsonObject getListMatchSuggested(long userId, int state,long page, long recordPerPage);
    JsonObject getListMatchSuggested(int state, long page, long recordPerPage);

    JsonObject cancelMatchSuggest(long matchId);

    JsonObject confirmMatchSuggest(JsonObject info);

    JsonObject deleteMatchSuggest(long matchId);
}
