package vn.vietdefi.aoe.vertx.router.donate;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.donate.DonateConstants;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class  DonateRouter {

    public static void donateGamer(RoutingContext rc) {
        try {
            long userId = Long.parseLong(rc.request().getHeader("userid"));
            String request = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            long targetId = data.get("targetId").getAsLong();
            long star = data.get("star").getAsLong();
            String message = data.get("message").getAsString();
            JsonObject response = AoeServices.donateService.donate(userId, star, StarConstant.SERVICE_DONATE_GAMER, targetId, message);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void donateCaster(RoutingContext rc) {
        try {
            long userId = Long.parseLong(rc.request().getHeader("userid"));
            String request = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            long targetId = data.get("targetId").getAsLong();
            long star = data.get("star").getAsLong();
            String message = data.get("message").getAsString();
            JsonObject response = AoeServices.donateService.donate(userId, star, StarConstant.SERVICE_DONATE_CASTER, targetId, message);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void donateMatch(RoutingContext rc) {
        try {
            long userId = Long.parseLong(rc.request().getHeader("userid"));
            String request = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            long targetId = data.get("targetId").getAsLong();
            long star = data.get("star").getAsLong();
            String message = data.get("message").getAsString();
            JsonObject response = AoeServices.donateService.donate(userId, star, StarConstant.SERVICE_DONATE_MATCH, targetId, message);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void listDonate(RoutingContext rc) {
        try {
            long targetId = Long.parseLong(rc.request().getParam("target_id"));
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            int service = Integer.parseInt(rc.request().getParam("service"));
            JsonObject response = AoeServices.donateService.listDonateByTargetId(service, targetId, page, DonateConstants.DEFAULT_RECORD_PER_PAGE);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }
    public static void listTopDonate(RoutingContext rc) {
        try {
            long targetId = Long.parseLong(rc.request().getParam("target_id"));
            long from = Long.parseLong(rc.request().getParam("from", "0"));
            long to = Long.parseLong(rc.request().getParam("to", "0"));
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            JsonObject response = AoeServices.donateService.listTopDonateByTargetId(targetId, from, to, page, DonateConstants.DEFAULT_RECORD_PER_PAGE);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void listAllTopDonate(RoutingContext rc) {
        try {
            long from = Long.parseLong(rc.request().getParam("from","0"));
            long to = Long.parseLong(rc.request().getParam("to","0"));
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            JsonObject response = AoeServices.donateService.listAllTopDonate(from, to, page, DonateConstants.DEFAULT_RECORD_PER_PAGE);
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
