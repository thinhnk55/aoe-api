package vn.vietdefi.aoe.services.match;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class MatchService implements IMatchService {
    @Override
    public JsonObject createMatch(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject data = new JsonObject();

            DebugLogger.info("{}", json);
            data.addProperty("format", json.get("format").getAsInt());
            data.addProperty("type", json.get("type").getAsInt());
            data.addProperty("star_default", json.get("star_default").getAsLong());
            data.add("detail", json.get("detail").getAsJsonObject());
            data.addProperty("time_expired", json.get("time_expired").getAsLong());
            data.addProperty("suggester_id", json.get("userid").getAsLong());
            data.addProperty("state", MatchConstants.STATE_VOTING);
            data.addProperty("create_time", System.currentTimeMillis());
            data.add("team_player", json.get("team_player").getAsJsonArray());
            bridge.insertObjectToDB("aoe_match", data);
            JsonObject response =  MatchGamer.createTeamPlayer(data.get("team_player").getAsJsonArray(),
                    data.get("id").getAsLong());
            if (!BaseResponse.isSuccessFullMessage(response)){
                response = updateState(data.get("id").getAsLong(),MatchConstants.STATE_ERROR_CREATE_TEAM);
                if (!BaseResponse.isSuccessFullMessage(response)){
                    return response;
                }else {
                    return BaseResponse.createFullMessageResponse(15, "create_team_error");
                }
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

//    private JsonObject createDetail(JsonObject json) {
//        JsonObject detail = new JsonObject();
//        detail.addProperty("description", json.get("description").getAsString());//
//        detail.addProperty("percent_for_gamer", json.get("percent_for_gamer").getAsString());
//        detail.addProperty("percent_for_viewer", json.get("percent_for_viewer").getAsString());
//        detail.addProperty("percent_for_organizers", json.get("percent_for_organizers").getAsString());
//        detail.addProperty("link_livestream", "");
//        if (json.has("link_livestream")) {
//            detail.addProperty("link_livestream", json.get("link_livestream").getAsString());
//        }
//        detail.add("result", new JsonArray());
//        if (json.has("result")) {
//            detail.add("result", json.get("result"));
//        }
//        detail.addProperty("match_date", 0);
//        if (json.has("match_date")) {
//            detail.addProperty("match_date", json.get("match_date").getAsLong());
//        }
//        return detail;
//    }


    public JsonObject updateMatch(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject updateToDb = new JsonObject();
            updateToDb.addProperty("id", json.get("id").getAsLong());
            updateToDb.add("detail", json.get("detail").getAsJsonObject());
            updateToDb.addProperty("type", json.get("type").getAsInt());
            updateToDb.addProperty("format", json.get("format").getAsInt());
            updateToDb.addProperty("time_expired", json.get("time_expired").getAsLong());
            updateToDb.addProperty("star_default", json.get("star_default").getAsLong());
            updateToDb.add("team_player", json.get("team_player").getAsJsonArray());
            bridge.updateObjectToDb("aoe_match", "id", updateToDb);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getListMatch(int state, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();

            long offset = (page - 1) * recordPerPage;
            JsonObject result = new JsonObject();
            StringBuilder dataQuery = new StringBuilder("SELECT * FROM aoe_match WHERE state = ? ");
            if (state < MatchConstants.STATE_CANCELLED) {
                dataQuery.append("ORDER BY ABS(star_current-star_default)  LIMIT ? OFFSET ?");
            } else {
                dataQuery.append("ORDER BY time_expired DESC LIMIT ? OFFSET ?");
            }
            JsonArray data = bridge.query(String.valueOf(dataQuery), state, recordPerPage, offset);
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
                return BaseResponse.createFullMessageResponse(10, "match_not_found");
            }
            JsonObject user = AoeServices.profileService.getUserProfileByUserId(data.get("suggester_id").getAsLong());
            data.addProperty("suggester", user.get("data").getAsJsonObject().get("nick_name").getAsString());
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateResult(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long matchId = json.get("match_id").getAsLong();
            String query = "SELECT * FROM aoe_match WHERE id = ?";
            JsonObject data = bridge.queryOne(query, matchId);
            if (data == null) {
                return BaseResponse.createFullMessageResponse(10, "match_not_found");
            }
            if (data.get("state").getAsInt() != MatchConstants.STATE_FINISHED) {
                return BaseResponse.createFullMessageResponse(11, "Invalid_operation");
            }
            data.get("detail").getAsJsonObject().add("result", json.get("result").getAsJsonArray());
            bridge.updateObjectToDb("aoe_match", "id", data);
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
            String query = "SELECT * FROM aoe_match WHERE star_current < star_default AND state = 1 ORDER BY star_current DESC";
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
            String query = "UPDATE aoe_match SET state =? WHERE id =?";
            bridge.update(query, state, matchId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject lockMatchForUpcoming(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long matchId = json.get("match_id").getAsLong();
            JsonObject data = getById(matchId);
            JsonObject res = checkAction(data, MatchConstants.STATE_STOP_VOTING);
            if (!BaseResponse.isSuccessFullMessage(res)) {
                return res;
            }
            data = data.get("data").getAsJsonObject();
            JsonObject updateIntoDb = new JsonObject();
            updateIntoDb.addProperty("id",matchId);
            updateIntoDb.add("detail",data.get("detail"));
            updateIntoDb.get("detail").getAsJsonObject().addProperty("match_date", json.get("match_date").getAsLong());
            updateIntoDb.addProperty("state", MatchConstants.STATE_STOP_VOTING);
            updateIntoDb.addProperty("time_expired", System.currentTimeMillis());
            bridge.updateObjectToDb("aoe_match", "id", updateIntoDb);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    private JsonObject checkAction(JsonObject data, int stateUpdate) {
        if (!BaseResponse.isSuccessFullMessage(data)) {
            return BaseResponse.createFullMessageResponse(10, "not_found_match");
        }
        data = data.get("data").getAsJsonObject();
        int state = data.get("state").getAsInt();
        if (state >= stateUpdate || (stateUpdate - state) >= 2) {
            return BaseResponse.createFullMessageResponse(11, "Invalid_operation");
        }
        return BaseResponse.createFullMessageResponse(0, "success");
    }


    @Override
    public JsonObject startMatch(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long matchId = json.get("match_id").getAsLong();
            JsonObject data = getById(matchId);
            JsonObject res = checkAction(data, MatchConstants.STATE_PLAYING);
            if (!BaseResponse.isSuccessFullMessage(res)) {
                return res;
            }
            data = data.get("data").getAsJsonObject();
            JsonObject updateIntoDb = new JsonObject();
            updateIntoDb.addProperty("id",matchId);
            updateIntoDb.add("detail",data.get("detail"));
            updateIntoDb.get("detail").getAsJsonObject().addProperty("link_livestream", json.get("link_livestream").getAsString());
            updateIntoDb.addProperty("state", MatchConstants.STATE_PLAYING);
            updateIntoDb.addProperty("time_expired", System.currentTimeMillis());
            bridge.updateObjectToDb("aoe_match", "id", updateIntoDb);
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
            JsonObject res = checkAction(data, MatchConstants.STATE_FINISHED);
            if (!BaseResponse.isSuccessFullMessage(res)) {
                return res;
            }
            JsonObject updateDb =new JsonObject();
            data = data.get("data").getAsJsonObject();
            data.get("detail").getAsJsonObject().add("result", json.get("result").getAsJsonArray());
            updateDb.addProperty("id",data.get("id").getAsLong());
            updateDb.add("detail",data.get("detail").getAsJsonObject());
            updateDb.addProperty("state", MatchConstants.STATE_FINISHED);
            bridge.updateObjectToDb("aoe_match", "id", updateDb);
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
            if (!BaseResponse.isSuccessFullMessage(data)) {
                return data;
            }
            int state = data.get("data").getAsJsonObject().get("state").getAsInt();
            if (state > MatchConstants.STATE_STOP_VOTING) {
                return BaseResponse.createFullMessageResponse(11,
                        "cancel_reject", data);
            }
            String query = "UPDATE aoe_match SET state = ? ,time_expired = ? WHERE id = ?";
            bridge.update(query, MatchConstants.STATE_CANCELLED, System.currentTimeMillis(), matchId);
            //TODO: refund donate
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject addStarCurrentMatch(long matchId, long amount) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE aoe_match SET star_current = star_current + ? WHERE id = ?";
            bridge.update(query, amount, matchId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public long totalMatch(long userId) {
        return 0;
    }

    public JsonObject checkAcceptDonate(long matchId) {
        try {
            JsonObject response = getById(matchId);
            if(BaseResponse.isSuccessFullMessage(response)){
                int state = response.getAsJsonObject("data")
                        .get("state").getAsInt();
                if(state == MatchConstants.STATE_VOTING){
                    return response;
                }else{
                    return BaseResponse.createFullMessageResponse(10, "donate_reject");
                }
            }else{
                return response;
            }
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject confirmMatch(JsonObject json) {
        return null;
    }


    /*These function user for TEST only. In real situation these actions is prohibited*/
    @Override
    public JsonObject deleteMatch(long matchId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "DELETE FROM aoe_match WHERE id = ?";
            bridge.update(query, matchId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
