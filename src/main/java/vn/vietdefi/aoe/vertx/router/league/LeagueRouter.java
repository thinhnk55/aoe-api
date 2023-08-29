package vn.vietdefi.aoe.vertx.router.league;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.league.LeagueConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class LeagueRouter {
    public static void create(RoutingContext rc){
        try {
            String body = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(body);
            JsonObject response = AoeServices.leagueService.createLeague(data);
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
            String body = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(body);
            JsonObject response = AoeServices.leagueService.updateLeague(data);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void getInfo(RoutingContext rc){
        try {
            long leagueId = Long.parseLong(rc.request().getParam("id"));
            JsonObject response = AoeServices.leagueService.getLeagueInfo(leagueId);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void getListByState(RoutingContext rc){
        try {
            int state = Integer.parseInt(rc.request().getParam("state"));
            int page = Integer.parseInt(rc.request().getParam("page"));
            JsonObject response = AoeServices.leagueService.getListLeagueByState(state, page, LeagueConstant.DEFAULT_RECORD_PER_PAGE);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void getListAll(RoutingContext rc){
        try {
            int page = Integer.parseInt(rc.request().getParam("page"));
            JsonObject response = AoeServices.leagueService.getListLeague(page, LeagueConstant.DEFAULT_RECORD_PER_PAGE);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void stopVoteLeague(RoutingContext rc){
        try {
            String body = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(body);
            JsonObject response = AoeServices.leagueService.stopVoteLeague(data);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void startLeague(RoutingContext rc){
        try {
            String body = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(body);
            JsonObject response = AoeServices.leagueService.startLeague(data);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void endLeague(RoutingContext rc){
        try {
            String body = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(body);
            JsonObject response = AoeServices.leagueService.endLeague(data);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void cancelLeague(RoutingContext rc){
        try {
            String body = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(body);
            long leagueId = data.get("id").getAsLong();
            JsonObject response = AoeServices.leagueService.cancelLeague(leagueId);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void deleteLeague(RoutingContext rc){
        try {
            String body = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(body);
            long leagueId = data.get("id").getAsLong();
            JsonObject response = AoeServices.leagueService.deleteLeague(leagueId);
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
