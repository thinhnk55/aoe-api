package vn.vietdefi.aoe.services.league;

import com.google.gson.JsonObject;

public interface ILeagueService {
    JsonObject createLeague(JsonObject data);
    JsonObject updateLeague(JsonObject data);
    JsonObject getLeagueInfo(long id);
    JsonObject getListLeagueByState(int state, int page, int recordPerPage);
    JsonObject pendLeague(JsonObject data);
    JsonObject startLeague(long id);
    JsonObject endLeague(JsonObject data);
    JsonObject cancelLeague(long id);
}
