package vn.vietdefi.aoe.vertx.router.matchsuggest;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.match.MatchConstants;
import vn.vietdefi.aoe.services.matchsuggest.MatchSuggestConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class MatchSuggestRouter {

    public static void createMatchSuggest(RoutingContext rc){
        try {
            long userid = Long.parseLong(rc.request().getHeader("userid"));
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            JsonObject response = AoeServices.suggestService.createMatchSuggest(userid, json);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void updateMatchSuggest(RoutingContext rc){
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            long matchSuggesterId = json.get("id").getAsLong();
            JsonObject response = AoeServices.suggestService.updateMatchSuggest(matchSuggesterId,json);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }
    public static void confirmMatch(RoutingContext rc){
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            JsonObject response = AoeServices.suggestService.confirmMatchSuggest(json);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }
    public static void cancelMatchSuggest(RoutingContext rc){
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            long matchId = json.get("match_id").getAsLong();
            JsonObject response = AoeServices.suggestService.cancelMatchSuggest(matchId);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }
    public static void getListMatchSuggested(RoutingContext rc) {
        try {
            long userid = Long.parseLong(rc.request().getHeader("userid"));
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            JsonObject response = AoeServices.suggestService.getListMatchSuggested(userid, page, MatchSuggestConstant.DEFAULT_RECORD_PER_PAGE);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }
    public static void getMatchSuggestInfo(RoutingContext rc) {
        try {
            long matchId = Long.parseLong(rc.request().getParam("match_id"));
            JsonObject response = AoeServices.suggestService.getMatchSuggest(matchId);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    /*For test*/
    public static void deleteMatchSuggest(RoutingContext rc){
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            long matchId = json.get("match_id").getAsLong();
            JsonObject response = AoeServices.suggestService.deleteMatchSuggest(matchId);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }
}
