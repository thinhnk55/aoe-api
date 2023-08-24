package vn.vietdefi.aoe.services.league;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.match.MatchConstants;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class LeagueService implements ILeagueService{
    @Override
    public JsonObject createLeague(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            data.addProperty("start_date", 0);
            data.addProperty("state", LeagueConstants.STATE_VOTING);
            data.addProperty("create_time", System.currentTimeMillis());
            bridge.insertObjectToDB("aoe_league", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateLeague(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            bridge.updateObjectToDb("aoe_league", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getLeagueInfo(long id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_league WHERE id = ?";
            JsonObject data = bridge.queryOne(query, id);
            if (data == null)
                return BaseResponse.createFullMessageResponse(10, "league_not_found");
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getListLeagueByState(int state, int page, int recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            StringBuilder query = new StringBuilder("SELECT * FROM aoe_league WHERE state = ? ");
            if (state != LeagueConstants.STATE_CANCELLED) {
                query.append("ORDER BY (star_current - star_default_online) LIMIT ? OFFSET ?");
            } else {
                query.append("ORDER BY id DESC LIMIT ? OFFSET ?");
            }
            JsonObject data = new JsonObject();
            JsonArray leagues = bridge.query(query.toString(), state, recordPerPage, (page - 1) * recordPerPage);
            data.add("league", leagues);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject stopVoteLeague(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long leagueId = data.get("id").getAsLong();
            JsonObject checkState = checkState(leagueId, LeagueConstants.STATE_STOP_VOTING);
            if (!BaseResponse.isSuccessFullMessage(checkState))
                return checkState;
            long startDate = data.get("start_date").getAsLong();
            String query = "UPDATE aoe_league SET start_date = ?, state = ? WHERE id = ?";
            bridge.update(query, startDate, LeagueConstants.STATE_STOP_VOTING, leagueId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject startLeague(long id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject checkState = checkState(id, LeagueConstants.STATE_PLAYING);
            if (!BaseResponse.isSuccessFullMessage(checkState))
                return checkState;
            String query = "UPDATE aoe_league SET state = ? WHERE id = ?";
            bridge.update(query, LeagueConstants.STATE_PLAYING, id);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject endLeague(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long leagueId = data.get("id").getAsLong();
            JsonObject checkState = checkState(leagueId, LeagueConstants.STATE_FINISHED);
            if (!BaseResponse.isSuccessFullMessage(checkState))
                return checkState;
            JsonArray result = data.get("result").getAsJsonArray();
            JsonObject league = getLeagueInfo(leagueId);
            JsonObject detail = league.getAsJsonObject("data").get("detail").getAsJsonObject();
            detail.add("result", result);
            String query = "UPDATE aoe_league SET detail = ?, state = ? WHERE id = ?";
            bridge.update(query, detail, LeagueConstants.STATE_FINISHED, leagueId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject cancelLeague(long id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject checkState = checkState(id, LeagueConstants.STATE_CANCELLED);
            if (!BaseResponse.isSuccessFullMessage(checkState))
                return checkState;
            String query = "UPDATE aoe_league SET state = ? WHERE id = ?";
            bridge.update(query, LeagueConstants.STATE_CANCELLED, id);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject totalLeagueComplete() {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT COUNT(*) AS total_league_complete FROM aoe_league WHERE state = ?";
            JsonObject result = bridge.queryOne(query,LeagueConstants.STATE_FINISHED);
            return BaseResponse.createFullMessageResponse(0, "success",result);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject checkState (long id, int state) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_league WHERE id = ?";
            JsonObject data = bridge.queryOne(query, id);
            if (data == null)
                return BaseResponse.createFullMessageResponse(10, "league_not_found");
            int oldState = data.get("state").getAsInt();
            if (oldState > state || state - oldState >= 2)
                return BaseResponse.createFullMessageResponse(11, "update_reject");

            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    /*For test*/
    @Override
    public JsonObject deleteLeague(long id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "DELETE FROM aoe_league WHERE id = ?";
            bridge.update(query, id);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

}
