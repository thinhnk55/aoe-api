package vn.vietdefi.aoe.services.match;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class MatchService implements IMatchService{
    @Override
    public JsonObject adminCreateMatch(JsonObject json, long adminId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject insertToDB = new JsonObject();
            insertToDB.addProperty("format", json.get("format").getAsInt());
            insertToDB.addProperty("type", json.get("type").getAsInt());
            insertToDB.addProperty("star_default", json.get("star_default").getAsString());
            insertToDB.add("detail", createDetail(json));
            insertToDB.addProperty("time_expired", json.get("time_expired").getAsLong());
            insertToDB.addProperty("suggester_id",adminId);
            insertToDB.addProperty("state", MatchConstants.MATCH_VOTING);
            insertToDB.addProperty("create_time",System.currentTimeMillis());
            insertToDB.add("team_player",json.get("team_player").getAsJsonArray());
            bridge.insertObjectToDB("`aoe_match`", "id", insertToDB);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    private JsonObject createDetail(JsonObject json) {
        JsonObject detail = new JsonObject();
        detail.addProperty("description", json.get("description").getAsString());//
        detail.addProperty("percent_for_gamer", json.get("percent_for_gamer").getAsString());
        detail.addProperty("percent_for_viewer", json.get("percent_for_viewer").getAsString());
        detail.addProperty("percent_for_organizers", json.get("percent_for_organizers").getAsString());
        detail.addProperty("link_livestream", "");
        if (json.has("link_livestream")) {
            detail.addProperty("link_livestream", json.get("link_livestream").getAsString());
        }
        detail.add("result",new JsonArray());
        if (json.has("result")) {
            detail.add("result", json.get("result"));
        }
        detail.addProperty("match_date",0);
        if (json.has("match_date")) {
            detail.addProperty("match_date", json.get("match_date").getAsLong());
        }
        return detail;
    }


    public JsonObject updateMatch(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject updateToDb = createDetail(json);
            updateToDb.addProperty("time_expired", json.get("time_expired").getAsLong());
            bridge.updateObjectToDb("aoe_match","id",json);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getListMatch(int state , long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();

            long offset = (page - 1) * recordPerPage;
            String countQuery = "SELECT COUNT(*) AS total_rows FROM aoe_match WHERE `state ` = ?";
            JsonObject result = new JsonObject();
            result.addProperty("total_page", bridge.queryInteger(countQuery, state )/recordPerPage + 1);
            StringBuilder dataQuery = new StringBuilder("SELECT * FROM aoe_match WHERE `state ` = ? ");
            if(state  < MatchConstants.MATCH_CANCELLED){
                dataQuery.append("ORDER BY ABS(star_current-star_default)  LIMIT ? OFFSET ?");
            }else {
                dataQuery.append("ORDER BY time_expired DESC LIMIT ? OFFSET ?");
            }
            JsonArray data = bridge.query(String.valueOf(dataQuery), state , recordPerPage, offset);
            result.add("match", data);
            return BaseResponse.createFullMessageResponse(0, "success", result);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getById(long match_id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_match WHERE id = ?";
            JsonObject data = bridge.queryOne(query, match_id);
            if (data == null) {
                return BaseResponse.createFullMessageResponse(10, "not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateResult(long matchId, JsonArray json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject data = getById(matchId);
            JsonObject details = data.get("data").getAsJsonObject().get("detail").getAsJsonObject();
            details.add("result",json);
            data.add("detail",details);
            bridge.updateObjectToDb("aoe_match","id",data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public boolean checkMatchExists(long matchId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT id FROM aoe_match WHERE id = ?";
            JsonObject data = bridge.queryOne(query, matchId);
            return data != null;
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return false;
        }
    }


    public JsonObject getOutstandingMatch() {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_match WHERE star_current < star_default ORDER BY star_current DESC";
            JsonObject data = bridge.queryOne(query);
            if (data == null) {
                return BaseResponse.createFullMessageResponse(10, "not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateState(long matchId, int state) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String  query = "UPDATE aoe_match SET state =? WHERE id =?";
            bridge.update(query,state,matchId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject lockMatchForUpcoming (JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long matchId = json.get("match_id").getAsLong();
            JsonObject data = getById(matchId);
            JsonObject res = checkAction(data);
            if (!BaseResponse.isSuccessFullMessage(res)){
                return res;
            }
            data.get("detail").getAsJsonObject().addProperty("match_date", json.get("match_date").getAsLong());
            data.addProperty("state", MatchConstants.MATCH_STOP_VOTING);
            data.addProperty("time_expired",System.currentTimeMillis());
            bridge.updateObjectToDb("aoe_match","id", data);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    private JsonObject checkAction(JsonObject data) {
        if (!BaseResponse.isSuccessFullMessage(data)){
            return BaseResponse.createFullMessageResponse(10, "not_found_match");
        }
        data = data.get("data").getAsJsonObject();
        int state = data.get("state").getAsInt();
        if (state >= MatchConstants.MATCH_STOP_VOTING) {
            return BaseResponse.createFullMessageResponse(11, "Invalid operation");
         }
        return BaseResponse.createFullMessageResponse(0, "success");
    }


    @Override
    public JsonObject startMatch(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long matchId = json.get("match_id").getAsLong();
            JsonObject data = getById(matchId);
            JsonObject res = checkAction(data);
            if (!BaseResponse.isSuccessFullMessage(res)){
                return res;
            }
            data.get("detail").getAsJsonObject().addProperty("match_date", json.get("match_date").getAsLong());
            data.addProperty("state", MatchConstants.MATCH_ONGOING);
            data.addProperty("time_expired",System.currentTimeMillis());
            bridge.updateObjectToDb("aoe_match","id", data);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject endMatch(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long matchId = json.get("match_id").getAsLong();
            JsonObject data = getById(matchId);
            JsonObject res = checkAction(data);
            if (!BaseResponse.isSuccessFullMessage(res)){
                return res;
            }
            data.get("detail").getAsJsonObject().add("result", json.get("result").getAsJsonArray());
            data.addProperty("state", MatchConstants.MATCH_FINISHED);
            bridge.updateObjectToDb("aoe_match","id", data);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject cancelMatch(long matchId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject data = getById(matchId);
            JsonObject res = checkAction(data);
            if (!BaseResponse.isSuccessFullMessage(res)){
                return res;
            }
            String query = "UPDATE aoe_match SET status = ? ,time_expired = ? WHERE id = ?";
            bridge.update(query,MatchConstants.MATCH_CANCELLED,matchId);
//            AoeServices.starService.refundMoney(matchId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject createMatchSuggest(JsonObject data, long userId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject insertToDB = new JsonObject();
            long star = data.get("star_donate").getAsLong();
            JsonObject user = AoeServices.starService.getStarWalletByUserId(userId);
            if (user.get("balance").getAsLong() < star){
                return BaseResponse.createFullMessageResponse(10, "balance_not_enough");
            }
            insertToDB.addProperty("format", data.get("format").getAsInt());
            insertToDB.addProperty("type", data.get("type").getAsInt());
            insertToDB.addProperty("create_time", System.currentTimeMillis());
            insertToDB.add("detail",data.get("description").getAsJsonObject());
            insertToDB.addProperty("suggester_id",userId);
            insertToDB.add("team_player", data.get("team_player").getAsJsonArray());
            insertToDB.addProperty("state", MatchConstants.MATCH_SUGGEST_PENDING);
            insertToDB.addProperty("star", star);
            bridge.insertObjectToDB("aoe_match_suggest", "id", insertToDB);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateMatchSuggest(long matchSuggestId, JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject matchSuggest = getMatchSuggest(matchSuggestId);
            if (!BaseResponse.isSuccessFullMessage(matchSuggest)){
                return BaseResponse.createFullMessageResponse(10,"not_found");
            }
            long userId = matchSuggest.get("suggest_id").getAsLong();
            long star = data.get("star_donate").getAsLong();
            JsonObject user = AoeServices.starService.getStarWalletByUserId(userId);
            if (user.get("balance").getAsLong() < star){
                return BaseResponse.createFullMessageResponse(10, "balance_not_enough");
            }
            JsonObject updateToDB = new JsonObject();
            updateToDB.addProperty("format", data.get("format").getAsInt());
            updateToDB.addProperty("type", data.get("type").getAsInt());
            updateToDB.add("detail",data.get("description").getAsJsonObject());
            updateToDB.add("team_player", data.get("team_player").getAsJsonArray());
            updateToDB.addProperty("star", star);
            bridge.updateObjectToDb("aoe_match_suggest", "id", updateToDB);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject getMatchSuggest(long id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_match_suggest WHERE id = ? ";
            JsonObject data = bridge.queryOne(query,id);
            if (data == null) {
                return BaseResponse.createFullMessageResponse(10, "not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }


    public JsonObject getListMatchSuggested(long userId,long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();

            long offset = (page - 1) * recordPerPage;
            String countQuery = "SELECT COUNT(*) AS total_rows FROM aoe_match_suggest WHERE suggest_id = ?";
            JsonObject result = new JsonObject();
            result.addProperty("total_page", bridge.queryInteger(countQuery, userId )/recordPerPage + 1);
            String dataQuery = new StringBuilder("SELECT * FROM aoe_match_suggest WHERE suggest_id = ? ORDER BY state DESC")
                    .append("LIMIT ? OFFSET ?").toString();

            JsonArray data = bridge.query(dataQuery,userId, recordPerPage, offset);
            result.add("match", data);
            return BaseResponse.createFullMessageResponse(0, "success", result);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateStarCurrentMatch(long matchId, long amount) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE aoe_match SET star_current = star_current + ? WHERE id = ?";
            bridge.update(query,amount,matchId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }


}
