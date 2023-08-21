package vn.vietdefi.aoe.vertx.router.caster;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.user.caster.CasterConstant;
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
            JsonObject response = AoeServices.casterService.updateCaster(json);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }
    public static void deleteCaster(RoutingContext rc) {
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            long casterId = json.get("caster_id").getAsLong();
            DebugLogger.info("{} dam xoa data", casterId);
            JsonObject response = AoeServices.casterService.deleteCaster(casterId);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }
    public static void getCasterByUserId(RoutingContext rc){
        try{
            long id = Long.parseLong(rc.request().getParam("caster_id"));
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

    public static void listCaster(RoutingContext rc) {
        try{
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            JsonObject response = AoeServices.casterService.listCaster(page, CasterConstant.DEFAULT_RECORD_PER_PAGE);
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
