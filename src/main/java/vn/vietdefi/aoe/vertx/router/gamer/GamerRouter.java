package vn.vietdefi.aoe.vertx.router.gamer;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class GamerRouter {
    public static void create(RoutingContext rc){
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            JsonObject response = AoeServices.gamerService.create(json);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }
    public static void updateInfo(RoutingContext rc){
        try{
            long userid = Long.parseLong(rc.request().getParam("id"));
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            json.addProperty("userid",userid);
            JsonObject response = AoeServices.gamerService.update(json);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }
    public static void getGamerByUserId(RoutingContext rc){
        try{
            long id = Long.parseLong(rc.request().getParam("id"));
            JsonObject response = AoeServices.gamerService.getGamerByUserId(id);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }

    public static void listGamerByMatchId(RoutingContext rc) {
        try{
            long matchId = Long.parseLong(rc.request().getParam("id"));
            JsonObject response = AoeServices.gamerService.listGamerByMatchId(matchId);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }

    public static void listGamer(RoutingContext rc) {
        try{
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            JsonObject response = AoeServices.gamerService.listGamer(page, StarConstant.DEFAULT_RECORD_PER_PAGE);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }

    public static void listGamerOfClan(RoutingContext rc) {
        try{
            long id = Long.parseLong(rc.request().getParam("id"));
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            JsonObject response = AoeServices.gamerService.listGamerOfClan(id, page, StarConstant.DEFAULT_RECORD_PER_PAGE);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }

    public static void listMatch(RoutingContext rc) {
        try{
            long id = Long.parseLong(rc.request().getParam("id"));
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            JsonObject response = AoeServices.gamerService.listMatch(id, page, StarConstant.DEFAULT_RECORD_PER_PAGE);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }


    public static void deleteGamer(RoutingContext rc) {
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            long Id = Long.parseLong(json.get("id").getAsString());
            JsonObject response = AoeServices.gamerService.deleteGamerById(Id);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }


}
