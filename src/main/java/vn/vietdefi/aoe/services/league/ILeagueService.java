package vn.vietdefi.aoe.services.league;

import com.google.gson.JsonObject;

public interface ILeagueService {
    JsonObject createLeague(JsonObject data);
    JsonObject updateLeague(JsonObject data);
    JsonObject getLeagueInfo(long id);
    JsonObject getListLeagueByState(int state, int page, int recordPerPage);
    JsonObject getListLeague(int page, int recordPerPage);
    JsonObject stopVoteLeague(JsonObject data);
    JsonObject startLeague(JsonObject data);
    JsonObject endLeague(JsonObject data);
    JsonObject cancelLeague(long id);
    JsonObject deleteLeague(long id);
    JsonObject totalLeagueComplete();
    JsonObject addStarForLeague(long leagueId, long amount);
    JsonObject getOutstandingLeague();

}
