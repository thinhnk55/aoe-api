package vn.vietdefi.aoe.vertx.router.clan;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class ClanRouter {
    public static void createClan(RoutingContext rc){
        try{
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            JsonObject response = AoeServices.clanService.createClan(json);
            rc.response().end(response.toString());
        }catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }
    public static void getInfoClan(RoutingContext rc){
        try{
            long clanId = Long.parseLong(rc.request().getParam("clanId"));
            JsonObject response = AoeServices.clanService.getClanInfo(clanId);
            rc.response().end(response.toString());
        }catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }

    public static void updateClan(RoutingContext rc){
        try{
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            JsonObject response = AoeServices.clanService.updateClan(json);
            rc.response().end(response.toString());
        }catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }

    public static void getListClan(RoutingContext rc) {
        try{
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            JsonObject response = AoeServices.clanService.getListClan(page,StarConstant.DEFAULT_RECORD_PER_PAGE);
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
