package vn.vietdefi.aoe.services.matchsuggest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class MatchSuggestService implements IMatchSuggestService{
    @Override
    public JsonObject createMatchSuggest(long suggester, JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long star = data.get("amount").getAsLong();
            if(star < MatchSuggestConstant.STAR_REQUIRE_TO_SUGGEST_A_MATCH){
                return BaseResponse.createFullMessageResponse(10, "star_reject");
            }
            JsonObject response = AoeServices.starService.exchangeStar(
                    suggester, StarConstant.SERVICE_SUGGEST_MATCH, -star, 0);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return BaseResponse.createFullMessageResponse(10, "star_reject");
            }
            data.addProperty("suggester_id",suggester);
            data.addProperty("create_time", System.currentTimeMillis());
            data.addProperty("state", MatchSuggestConstant.MATCH_SUGGEST_PENDING);
            bridge.insertObjectToDB("aoe_match_suggest", "id", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
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
            JsonObject response = getMatchSuggest(matchSuggestId);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return BaseResponse.createFullMessageResponse(10, "not_found");
            }
            JsonObject old = response.get("data").getAsJsonObject();
            long oldSuggester = old.get("suggester_id").getAsLong();
            long newSuggester = old.get("suggester_id").getAsLong();
            int oldState = old.get("state").getAsInt();
            int newState = data.get("state").getAsInt();
            int oldStar = old.get("amount").getAsInt();
            int newStar = data.get("amount").getAsInt();
            if(oldState != MatchSuggestConstant.MATCH_SUGGEST_PENDING
            && oldState != newState && oldSuggester != newSuggester && oldStar != newStar){
                return BaseResponse.createFullMessageResponse(11, "update_reject");
            }
            bridge.updateObjectToDb("aoe_match_suggest", "id", data);
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
            JsonObject data = new JsonObject();
            String dataQuery = "SELECT * FROM aoe_match_suggest WHERE suggester_id = ? ORDER BY state DESC LIMIT ? OFFSET ?";
            JsonArray array = bridge.query(dataQuery, userId, recordPerPage, offset);
            data.add("match", array);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject getListMatchSuggested(long page, long recordPerPage, int state) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            JsonObject data = new JsonObject();
            String dataQuery = "SELECT * FROM aoe_match_suggest WHERE state = ? ORDER BY id DESC LIMIT ? OFFSET ?";
            JsonArray array = bridge.query(dataQuery,state,recordPerPage, offset);
            data.add("match", array);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    public JsonObject cancelMatchSuggest(long matchId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject response = getMatchSuggest(matchId);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return response;
            }
            JsonObject data = response.getAsJsonObject("data");
            if (data.get("state").getAsInt()
                    != MatchSuggestConstant.MATCH_SUGGEST_PENDING) {
                return BaseResponse.createFullMessageResponse(11, "cancel_reject", data);
            }
            String query = "UPDATE aoe_match_suggest SET state = ? WHERE id = ?";
            bridge.update(query, MatchSuggestConstant.MATCH_SUGGEST_CANCELLED, matchId);
            long suggester = data.get("suggester_id").getAsLong();
            long star = data.get("amount").getAsLong();
            response = AoeServices.starService.exchangeStar(suggester, StarConstant.SERVICE_SUGGEST_MATCH_REFUND
                    ,star,matchId);
            if(!BaseResponse.isSuccessFullMessage(response)){
                DebugLogger.error("star_refund_error: {} {} {} {}", suggester, star, matchId, response);
            }
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject confirmMatchSuggest(JsonObject info) {
        try {
            long id = info.get("match_suggest_id").getAsLong();
            JsonObject response = getMatchSuggest(id);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return response;
            }
            JsonObject suggest = response.get("data").getAsJsonObject();
            if (suggest.get("state").getAsInt() != MatchSuggestConstant.MATCH_SUGGEST_PENDING) {
                return BaseResponse.createFullMessageResponse(11, "confirm_reject");
            }
            long suggester = suggest.get("suggester_id").getAsLong();
            long star = suggest.get("amount").getAsLong();
            if (!AoeServices.starService.checkStar(star, suggester)) {
                return BaseResponse.createFullMessageResponse(12, "balance_not_enough");
            }
            response = AoeServices.starService.exchangeStar(suggester,
                    StarConstant.SERVICE_SUGGEST_MATCH_REFUND
                    ,star, 0);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return BaseResponse.createFullMessageResponse(13, "refund_suggest_error");
            }
            JsonObject match = new JsonObject();
            match.addProperty("userid", suggester);
            match.addProperty("format", info.get("format").getAsInt());
            match.addProperty("type", info.get("type").getAsInt());
            match.addProperty("star_default", info.get("star_default").getAsLong());
            match.add("detail", info.get("detail").getAsJsonObject());
            match.addProperty("time_expired", info.get("time_expired").getAsLong());
            match.add("team_player", info.get("team_player").getAsJsonArray());
            response = AoeServices.matchService.createMatch(match);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return response;
            }
            long match_id = response.get("data").getAsJsonObject().get("id").getAsInt();
            response = AoeServices.donateService.donate(suggester, star,
                    StarConstant.SERVICE_DONATE_MATCH, match_id, "Donate suggest match");
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return BaseResponse.createFullMessageResponse(16, "donate_match_again_reject");
            }
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE aoe_match_suggest SET state = ? WHERE id = ?";
            bridge.update(query, MatchSuggestConstant.MATCH_SUGGEST_CONFIRM, id);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    /*For test*/
    @Override
    public JsonObject deleteMatchSuggest(long matchId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "DELETE FROM aoe_match_suggest WHERE id = ? ";
            bridge.queryOne(query, matchId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

}
