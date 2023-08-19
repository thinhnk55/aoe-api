package vn.vietdefi.aoe.services.match;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class MatchService implements IMatchService {
    @Override
    public JsonObject CreateMatch(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject insertToDB = new JsonObject();
            insertToDB.addProperty("format", json.get("format").getAsInt());
            insertToDB.addProperty("type", json.get("type").getAsInt());
            insertToDB.addProperty("star_default", json.get("star_default").getAsLong());
            insertToDB.add("detail", createDetail(json));
            insertToDB.addProperty("time_expired", json.get("time_expired").getAsLong());
            insertToDB.addProperty("suggester_id", json.get("userid").getAsLong());
            insertToDB.addProperty("state", MatchConstants.MATCH_VOTING);
            insertToDB.addProperty("create_time", System.currentTimeMillis());
            insertToDB.add("team_player", json.get("team_player").getAsJsonArray());
            bridge.insertObjectToDB("`aoe_match`", "id", insertToDB);
            return BaseResponse.createFullMessageResponse(0, "success", insertToDB);
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
        detail.add("result", new JsonArray());
        if (json.has("result")) {
            detail.add("result", json.get("result"));
        }
        detail.addProperty("match_date", 0);
        if (json.has("match_date")) {
            detail.addProperty("match_date", json.get("match_date").getAsLong());
        }
        return detail;
    }


    public JsonObject updateMatch(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject updateToDb = new JsonObject();
            updateToDb.add("detail", createDetail(json));
            updateToDb.addProperty("id", json.get("id").getAsLong());
            updateToDb.addProperty("type", json.get("type").getAsInt());
            updateToDb.addProperty("format", json.get("format").getAsInt());
            updateToDb.addProperty("time_expired", json.get("time_expired").getAsLong());
            updateToDb.addProperty("star_default", json.get("star_default").getAsLong());
            updateToDb.add("team_player", json.get("team_player"));


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
            String countQuery = "SELECT COUNT(*) AS total_rows FROM aoe_match WHERE state = ?";
            JsonObject result = new JsonObject();
            result.addProperty("total_page", bridge.queryInteger(countQuery, state) / recordPerPage + 1);
            StringBuilder dataQuery = new StringBuilder("SELECT * FROM aoe_match WHERE state = ? ");
            if (state < MatchConstants.MATCH_CANCELLED) {
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
                return BaseResponse.createFullMessageResponse(10, "not_found_match");
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
            JsonObject data = getById(matchId);
            if (!BaseResponse.isSuccessFullMessage(data)) {
                return data;
            }
            data = data.get("data").getAsJsonObject();
            if (data.get("state").getAsInt() != MatchConstants.MATCH_FINISHED) {
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
            JsonObject res = checkAction(data, MatchConstants.MATCH_STOP_VOTING);
            if (!BaseResponse.isSuccessFullMessage(res)) {
                return res;
            }
            data = data.get("data").getAsJsonObject();
            JsonObject updateIntoDb = new JsonObject();
            updateIntoDb.addProperty("id",matchId);
            updateIntoDb.add("detail",data.get("detail"));
            updateIntoDb.get("detail").getAsJsonObject().addProperty("match_date", json.get("match_date").getAsLong());
            updateIntoDb.addProperty("state", MatchConstants.MATCH_STOP_VOTING);
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
            JsonObject res = checkAction(data, MatchConstants.MATCH_ONGOING);
            if (!BaseResponse.isSuccessFullMessage(res)) {
                return res;
            }
            data = data.get("data").getAsJsonObject();
            JsonObject updateIntoDb = new JsonObject();
            updateIntoDb.addProperty("id",matchId);
            updateIntoDb.add("detail",data.get("detail"));
            updateIntoDb.get("detail").getAsJsonObject().addProperty("link_livestream", json.get("link_livestream").getAsLong());
            updateIntoDb.addProperty("state", MatchConstants.MATCH_STOP_VOTING);
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
            JsonObject res = checkAction(data, MatchConstants.MATCH_FINISHED);
            if (!BaseResponse.isSuccessFullMessage(res)) {
                return res;
            }
            JsonObject updateDb =new JsonObject();
            data = data.get("data").getAsJsonObject();
            data.get("detail").getAsJsonObject().add("result", json.get("result").getAsJsonArray());
            updateDb.addProperty("id",data.get("id").getAsLong());
            updateDb.add("detail",data.get("detail").getAsJsonObject());
            updateDb.addProperty("state", MatchConstants.MATCH_FINISHED);
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
            if (state == 5) {
                return BaseResponse.createFullMessageResponse(12, "match_cancelled");
            }
            if (state > MatchConstants.MATCH_ONGOING) {
                return BaseResponse.createFullMessageResponse(11, "match_on_going_or_finished");
            }
            String query = "UPDATE aoe_match SET state = ? ,time_expired = ? WHERE id = ?";
            bridge.update(query, MatchConstants.MATCH_CANCELLED, System.currentTimeMillis(), matchId);
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
            long star = data.get("amount").getAsLong();
            JsonObject user = AoeServices.starService.getStarWalletByUserId(userId);
            if (user.get("data").getAsJsonObject().get("balance").getAsLong() < star) {
                return BaseResponse.createFullMessageResponse(10, "balance_not_enough");
            }
            insertToDB.addProperty("format", data.get("format").getAsInt());
            insertToDB.addProperty("type", data.get("type").getAsInt());
            insertToDB.addProperty("create_time", System.currentTimeMillis());
            insertToDB.add("detail", data.get("description"));
            insertToDB.addProperty("suggester_id", userId);
            insertToDB.add("team_player", data.get("team_player").getAsJsonArray());
            insertToDB.addProperty("state", MatchConstants.MATCH_SUGGEST_PENDING);
            insertToDB.addProperty("star_current", star);
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
            if (!BaseResponse.isSuccessFullMessage(matchSuggest)) {
                return BaseResponse.createFullMessageResponse(10, "not_found");
            }
            matchSuggest = matchSuggest.get("data").getAsJsonObject();
            if (matchSuggest.get("state").getAsInt() != MatchConstants.MATCH_SUGGEST_PENDING) {
                return BaseResponse.createFullMessageResponse(11, "match_confirmed_or_cancelled");
            }
            long userId = matchSuggest.get("suggester_id").getAsLong();
            long star = data.get("amount").getAsLong();
            JsonObject user = AoeServices.starService.getStarWalletByUserId(userId);
            if (user.get("data").getAsJsonObject().get("balance").getAsLong() < star) {
                return BaseResponse.createFullMessageResponse(12, "balance_not_enough");
            }
            JsonObject updateToDB = new JsonObject();
            updateToDB.addProperty("id", matchSuggestId);
            updateToDB.addProperty("format", data.get("format").getAsInt());
            updateToDB.addProperty("type", data.get("type").getAsInt());
            updateToDB.add("detail", data.get("description"));
            updateToDB.add("team_player", data.get("team_player").getAsJsonArray());
            updateToDB.addProperty("star_current", star);
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
            JsonObject data = bridge.queryOne(query, id);
            if (data == null) {
                return BaseResponse.createFullMessageResponse(10, "not_found_match_suggest");
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }


    public JsonObject getListMatchSuggested(long userId, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();

            long offset = (page - 1) * recordPerPage;
            String countQuery = "SELECT COUNT(*) AS total_rows FROM aoe_match_suggest WHERE suggester_id = ?";
            JsonObject result = new JsonObject();
            result.addProperty("total_page", bridge.queryInteger(countQuery, userId) / recordPerPage + 1);
            String dataQuery = "SELECT * FROM aoe_match_suggest WHERE suggester_id = ? ORDER BY state DESC" + " LIMIT ? OFFSET ?";

            JsonArray data = bridge.query(dataQuery, userId, recordPerPage, offset);
            result.add("match", data);
            return BaseResponse.createFullMessageResponse(0, "success", result);
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
            if (!checkStateMatch(matchId, MatchConstants.MATCH_VOTING)) {
                return BaseResponse.createFullMessageResponse(10, "Donations_for_match_has_ended");
            }
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
    public JsonObject confirmMatch(JsonObject data) {
        try {
            long matchSuggestId = data.get("id").getAsInt();
            JsonObject match = getMatchSuggest(matchSuggestId);
            if (!BaseResponse.isSuccessFullMessage(match)) {
                return match;
            }
            match = match.get("data").getAsJsonObject();
            if (match.get("state").getAsInt() == MatchConstants.MATCH_SUGGEST_CONFIRM) {
                return BaseResponse.createFullMessageResponse(11, "match_confirmed");
            }
            long userId = match.get("suggester_id").getAsLong();
            long amount = match.get("star_current").getAsLong();
            if (!AoeServices.starService.checkStar(amount, userId)) {
                return BaseResponse.createFullMessageResponse(12, "balance_not_enough");
            }
            match.addProperty("userid", userId);
            match.addProperty("time_expired", data.get("time_expired").getAsLong());
            match.addProperty("star_default", data.get("star_default").getAsLong());
            match.addProperty("description", data.get("description").getAsString());//
            match.addProperty("percent_for_gamer", data.get("percent_for_gamer").getAsString());
            match.addProperty("percent_for_viewer", data.get("percent_for_viewer").getAsString());
            match.addProperty("percent_for_organizers", data.get("percent_for_organizers").getAsString());
            JsonObject check = CreateMatch(match);
            if (!BaseResponse.isSuccessFullMessage(check)) {
                return check;
            }
            long match_id = check.get("data").getAsJsonObject().get("id").getAsInt();
            JsonObject transaction = AoeServices.donateService.donate(userId, amount, StarConstant.SERVICE_DONATE_MATCH, match_id, "");
            if (!BaseResponse.isSuccessFullMessage(transaction)) {
                return transaction;
            }
            addStarCurrentMatch(match_id, amount);
            updateStateMatchSuggest(matchSuggestId, MatchConstants.MATCH_SUGGEST_CONFIRM);
            return BaseResponse.createFullMessageResponse(0, "success");

        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject cancelMatchSuggest(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long matchSuggestId = data.get("id").getAsInt();
            JsonObject matchSg = getMatchSuggest(matchSuggestId);
            if (!BaseResponse.isSuccessFullMessage(matchSg)) {
                return matchSg;
            }
            if (matchSg.get("data").getAsJsonObject().get("state").getAsInt() == MatchConstants.MATCH_SUGGEST_CONFIRM) {
                return BaseResponse.createFullMessageResponse(11, "match_confirmed");
            }

            if (matchSg.get("data").getAsJsonObject().get("state").getAsInt() == MatchConstants.MATCH_SUGGEST_CANCELLED) {
                return BaseResponse.createFullMessageResponse(12, "match_cancelled");
            }
            String query = "UPDATE aoe_match_suggest SET state = ? WHERE id = ?";
            bridge.update(query, MatchConstants.MATCH_SUGGEST_CANCELLED, matchSuggestId);
            updateStateMatchSuggest(matchSuggestId, MatchConstants.MATCH_SUGGEST_CANCELLED);

            return BaseResponse.createFullMessageResponse(0, "success");

        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    private void updateStateMatchSuggest(long id, int state) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();

            String query = "UPDATE aoe_match_suggest SET state = ? WHERE id = ?";
            bridge.update(query, state, id);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }


    private boolean checkStateMatch(long matchId, int state) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT state FROM aoe_match WHERE id = ?";
            int stateMatch = bridge.queryInteger(query, matchId);
            return stateMatch == state;

        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return false;
        }
    }


}
