package vn.vietdefi.aoe.services.match;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;
import vn.vietdefi.aoe.services.AoeServices;
public class MatchService implements IMatchService{
    @Override
    public JsonObject adminCreateMatch(JsonObject json, long adminId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject insertToDB = new JsonObject();
            insertToDB.addProperty("format", json.get("format").getAsInt());
            insertToDB.addProperty("type", json.get("type").getAsInt());
            insertToDB.addProperty("star_default", json.get("star_default").getAsString());
            insertToDB.addProperty("create_time", System.currentTimeMillis());
            insertToDB.add("detail", createDetail(json));
            insertToDB.addProperty("time_expired", json.get("time_expired").getAsLong());
            insertToDB.addProperty("suggester_id",adminId);
            insertToDB.addProperty("state", MatchConstants.MATCH_VOTING);
            insertToDB.addProperty("created_time",System.currentTimeMillis());
            bridge.insertObjectToDB("`match`", "match_id", insertToDB);
            bridge.insertObjectToDB("aoe_match", json);
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

    @Override
    public JsonObject updateMatch(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject updateToDb = createDetail(json);
            updateToDb.addProperty("time_expired", json.get("time_expired").getAsLong());
            bridge.updateObjectToDb("aoe_match","match_id", json);
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
            String countQuery = "SELECT COUNT(*) AS total_rows FROM `match` WHERE `state ` = ?";
            JsonObject result = new JsonObject();
            result.addProperty("total_page", bridge.queryInteger(countQuery, state )/recordPerPage + 1);
            StringBuilder dataQuery = new StringBuilder("SELECT * FROM `match` WHERE `state ` = ? ");
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
    public JsonObject getInfoMatch(long match_id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM `match` WHERE `match_id` = ?";
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
            JsonObject data = getInfoMatch(matchId);
            JsonObject details = data.get("data").getAsJsonObject().get("detail").getAsJsonObject();
            details.add("result",json);
            data.add("detail",details);
            bridge.updateObjectToDb("aoe_match","match_id",data);
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
            String query = "SELECT match_id FROM `match` WHERE `match_id` = ?";
            JsonObject data = bridge.queryOne(query, matchId);
            return data != null;
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return false;
        }
    }

    @Override
    public JsonObject getSuggestMatch() {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM `match` WHERE star_current < star_default ORDER BY star_current DESC";
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
            String  query = "UPDATE `match` SET state =? WHERE match_id =?";
            bridge.update(query,state,matchId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject lockMatchForUpcoming (long matchId, JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject data = getInfoMatch(matchId);
            JsonObject res = checkAction(data);
            if (!BaseResponse.isSuccessFullMessage(res)){
                return res;
            }
            data.get("detail").getAsJsonObject().addProperty("match_date", json.get("match_date").getAsLong());
            data.addProperty("state", MatchConstants.MATCH_STOP_VOTING);
            data.addProperty("time_expired",System.currentTimeMillis());
            bridge.updateObjectToDb("aoe_match","match_id", data);
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
    public JsonObject startMatch(long matchId, JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject data = getInfoMatch(matchId);
            JsonObject res = checkAction(data);
            if (!BaseResponse.isSuccessFullMessage(res)){
                return res;
            }
            data.get("detail").getAsJsonObject().addProperty("match_date", json.get("match_date").getAsLong());
            data.addProperty("state", MatchConstants.MATCH_ONGOING);
            data.addProperty("time_expired",System.currentTimeMillis());
            bridge.updateObjectToDb("aoe_match","match_id", data);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject endMatch(long matchId, JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject data = getInfoMatch(matchId);
            JsonObject res = checkAction(data);
            if (!BaseResponse.isSuccessFullMessage(res)){
                return res;
            }
            data.get("detail").getAsJsonObject().add("result", json.get("result").getAsJsonArray());
            data.addProperty("state", MatchConstants.MATCH_FINISHED);
            bridge.updateObjectToDb("aoe_match","match_id", data);
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
            JsonObject data = getInfoMatch(matchId);
            JsonObject res = checkAction(data);
            if (!BaseResponse.isSuccessFullMessage(res)){
                return res;
            }
            String query = "UPDATE `match` SET status = ? ,time_expired = ? WHERE match_id = ?";
            bridge.update(query,MatchConstants.MATCH_CANCELLED,matchId);
//            AoeServices.starService.refundMoney(matchId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }




}
