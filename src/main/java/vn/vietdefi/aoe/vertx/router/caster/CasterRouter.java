package vn.vietdefi.aoe.vertx.router.caster;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class CasterRouter {
    public static void createCaster(RoutingContext rc){
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            JsonObject response = AoeServices.casterService.createCaster(json);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }
    public static void updateCaster(RoutingContext rc){
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            long casterId = json.get("caster_id").getAsLong();
            JsonObject response = AoeServices.casterService.updateCaster(casterId,json);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }
    public static void deleteCaster(RoutingContext routingContext) {
        try {
            long casterId = Long.parseLong(routingContext.request().getParam("caster_id"));
            DebugLogger.info("{} dam xoa data", casterId);
            JsonObject response = AoeServices.casterService.deleteCaster(casterId);
            routingContext.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            routingContext.response().end(response.toString());
        }
    }
    public static void getCasterByUserId(RoutingContext rc){
        try{
            long id = Long.parseLong(rc.request().getParam("userId"));
            JsonObject response = AoeServices.casterService.getCasterByUserId(id);
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
