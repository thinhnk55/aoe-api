package vn.vietdefi.aoe.services.league;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public interface ILeagueService {
    JsonObject createLeague(JsonObject data);
    JsonObject updateLeague(JsonObject data);
    JsonObject getLeagueInfo(long id);
    JsonObject getListLeagueByState(int state, int page, int recordPerPage);
    JsonObject stopVoteLeague(JsonObject data);
    JsonObject startLeague(JsonObject data);
    JsonObject endLeague(JsonObject data);
    JsonObject cancelLeague(long id);
    JsonObject deleteLeague(long id);
    JsonObject totalLeagueComplete();
    JsonObject addStarForLeague(long leagueId, long amount);

}
