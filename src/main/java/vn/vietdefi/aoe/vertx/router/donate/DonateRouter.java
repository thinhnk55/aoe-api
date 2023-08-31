package vn.vietdefi.aoe.vertx.router.donate;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.donate.DonateConstant;
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
            JsonObject response = AoeServices.donateService.donate(userId, star, StarConstant.SERVICE_DONATE_MATCH, targetId, "");
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }
    public static void donateLeague(RoutingContext rc) {
        try {
            long userId = Long.parseLong(rc.request().getHeader("userid"));
            String request = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            long targetId = data.get("targetId").getAsLong();
            long star = data.get("star").getAsLong();
            JsonObject response = AoeServices.donateService.donate(userId, star, StarConstant.SERVICE_DONATE_LEAGUE, targetId, "");
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
            JsonObject response = AoeServices.donateService.listDonateByTargetId(service, targetId, page, DonateConstant.DEFAULT_RECORD_PER_PAGE);
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
            int service = Integer.parseInt(rc.request().getParam("service"));
            long page = 1;
            JsonObject response = AoeServices.donateService.listTopDonateByTargetId(service, targetId, page, DonateConstant.DEFAULT_RECORD_PER_PAGE);
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
            long page = 1;
            JsonObject response = AoeServices.donateService.listAllTopDonate(page, DonateConstant.DEFAULT_RECORD_PER_PAGE);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void listDonateOutstanding(RoutingContext rc) {
        try {
            JsonObject response = AoeServices.donateService.listDonateOutstanding();
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void filterListDonate(RoutingContext rc) {
        try {
            long page = Long.parseLong(rc.request().getParam("page","1"));
            String phone = rc.request().getParam("phone","");
            int service = Integer.parseInt(rc.request().getParam("service","0"));
            long from = Long.parseLong(rc.request().getParam("from","0"));
            long to = Long.parseLong(rc.request().getParam("to", String.valueOf(System.currentTimeMillis())));
            JsonObject response = AoeServices.donateService.filterListDonate(phone,from,to,service,page,DonateConstant.DEFAULT_RECORD_PER_PAGE);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }
    public static void filterStatisticDonate(RoutingContext rc) {
        try {
            long page = Long.parseLong(rc.request().getParam("page","1"));
            int service = Integer.parseInt(rc.request().getParam("service","0"));
            long targetId = Integer.parseInt(rc.request().getParam("target_id","0"));
            JsonObject response = AoeServices.donateService.filterStatisticDonate(service, targetId, page, DonateConstant.DEFAULT_RECORD_PER_PAGE);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }




    public static void statisticsDonate(RoutingContext rc){
        try {
            long from = Long.parseLong(rc.request().getParam("from","0"));
            long to = Long.parseLong(rc.request().getParam("to", String.valueOf(System.currentTimeMillis())));
            JsonObject response = AoeServices.donateService.statisticTotalDonate(from,to);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }


    public static void statisticDonateByUserId(RoutingContext rc){
        try {
            JsonObject response = AoeServices.donateService.statisticDonateByUserId();
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void detailDonateById(RoutingContext rc) {
        try {
            long id = Long.parseLong(rc.request().getParam("id"));
            JsonObject response = AoeServices.donateService.getDetailDonateById(id);
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
