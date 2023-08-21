package vn.vietdefi.aoe.vertx.router.match;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.match.MatchConstants;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class MatchRouter {

    public static void create(RoutingContext rc){
        try {
            long adminId = Long.parseLong(rc.request().getHeader("userid"));
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            json.addProperty("userid",adminId);
            JsonObject response = AoeServices.matchService.createMatch(json);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void update(RoutingContext rc){
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            JsonObject response = AoeServices.matchService.updateMatch(json);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void getById(RoutingContext rc){
        try {
            long id =  Long.parseLong(rc.request().getParam("matchId"));
            JsonObject response = AoeServices.matchService.getById(id);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void getListMatch(RoutingContext rc){
        try {
            int state =  Integer.parseInt(rc.request().getParam("state"));
            long page = Long.parseLong(rc.request().getParam("page"));
            JsonObject response = AoeServices.matchService.getListMatch(state,page, MatchConstants.ITEMS_PER_PAGE);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void getOutstandingMatch(RoutingContext rc){
        try {
            JsonObject response = AoeServices.matchService.getOutstandingMatch();
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void StopVotingMatch(RoutingContext rc){
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            JsonObject response = AoeServices.matchService.lockMatchForUpcoming(json);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void startMatch(RoutingContext rc){
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            JsonObject response = AoeServices.matchService.startMatch(json);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }
    public static void endMatch(RoutingContext rc){
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            JsonObject response = AoeServices.matchService.endMatch(json);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void updateResult(RoutingContext rc){
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            long matchId = json.get("match_id").getAsLong();
            JsonObject response = AoeServices.matchService.updateResult(json);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void cancelMatch(RoutingContext rc){
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            long matchId = json.get("match_id").getAsLong();
            JsonObject response = AoeServices.matchService.cancelMatch(matchId);
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
            JsonObject response = AoeServices.matchService.confirmMatch(json);
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
            long id = Long.parseLong(rc.request().getParam("id"));
            JsonObject response = AoeServices.suggestService.cancelMatchSuggest(id);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }




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

    public static void getListMatchSuggested(RoutingContext rc) {
        try {
            long userid = Long.parseLong(rc.request().getHeader("userid"));
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            JsonObject response = AoeServices.suggestService.getListMatchSuggested(userid, page, MatchConstants.ITEMS_PER_PAGE);
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
