package vn.vietdefi.aoe.vertx.router.gamer;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.user.gamer.GamerConstant;
import vn.vietdefi.api.services.ApiServices;
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
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
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

    public static void listGamer(RoutingContext rc) {
        try{
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            JsonObject response = AoeServices.gamerService.listGamer(page, GamerConstant.DEFAULT_RECORD_PER_PAGE);
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
            String username = json.get("username").getAsString();
            long id = ApiServices.authService.getUserIdByUserName(username);
            if(id != 0) {
                JsonObject response = AoeServices.gamerService.deleteGamerByUserId(id);
                rc.response().end(response.toString());
            }else{
                rc.response().end(BaseResponse.createFullMessageResponse(10, "not_found").toString());
            }
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }

    public static void listGamerOfClan(RoutingContext rc) {
        try{
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            long clanId = Long.parseLong(rc.request().getParam("clan_id"));
            JsonObject response = AoeServices.gamerService.listGamerByClanId(clanId, page, GamerConstant.DEFAULT_RECORD_PER_PAGE);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }

    public static void getGamerByName(RoutingContext rc) {
        try{
            String name = rc.request().getParam("name");
            JsonObject response = AoeServices.gamerService.getGamerByNickName(name);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }
}
