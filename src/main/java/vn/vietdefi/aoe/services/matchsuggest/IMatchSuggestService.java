package vn.vietdefi.aoe.services.matchsuggest;

import com.google.gson.JsonObject;

public interface IMatchSuggestService {
    JsonObject createMatchSuggest(long suggester, JsonObject data);
    JsonObject updateMatchSuggest( long id, JsonObject data);
    JsonObject cancelMatchSuggest(long id);
    JsonObject getListMatchSuggested(long userId,long page, long recordPerPage);

    JsonObject confirmMatchSuggest(JsonObject info);
}
