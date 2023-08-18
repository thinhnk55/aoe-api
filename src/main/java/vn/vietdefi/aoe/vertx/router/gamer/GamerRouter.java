package vn.vietdefi.aoe.vertx.router.gamer;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
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
    public static void deleteAccountant(RoutingContext rc){
        try{
            long userid = Long.parseLong(rc.request().getParam("gamerId"));
            JsonObject response = AoeServices.gamerService.deleteAccountant(userid);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }
    public static void updateInfo(RoutingContext rc){
        try{
            long userid = Long.parseLong(rc.request().getParam("gamerId"));
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            json.addProperty("userid",userid);
            JsonObject response = AoeServices.gamerService.updateInfo(json);
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
