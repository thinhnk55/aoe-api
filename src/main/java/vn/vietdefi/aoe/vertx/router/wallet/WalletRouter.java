package vn.vietdefi.aoe.vertx.router.wallet;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;

public class WalletRouter {
    public static void getWallet(RoutingContext rc) {
        try {
            long id = Long.parseLong(rc.request().getHeader("userId"));
            JsonObject response = AoeServices.starService.getStarWalletByUserId(id);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void listRecharge(RoutingContext rc) {
        try {
            long id = Long.parseLong(rc.request().getHeader("userId"));
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            JsonObject response = AoeServices.starService.listStarTransactionOfUserByService(id, StarConstant.SERVICE_STAR_RECHARGE, page, StarConstant.DEFAULT_RECORD_PER_PAGE);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void listTransaction(RoutingContext rc) {
        try {
            long id = Long.parseLong(rc.request().getHeader("userId"));
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            JsonObject response = AoeServices.starService.listStarTransactionOfUser(id, page, StarConstant.DEFAULT_RECORD_PER_PAGE);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void exchangeStar(RoutingContext rc) {
        try {
            //test
            JsonObject response = AoeServices.starService.exchangeStar(1000,StarConstant.SERVICE_STAR_RECHARGE, "0964714430", 1);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void getTransaction(RoutingContext rc) {
        try {
            long id = Long.parseLong(rc.request().getParam("id"));
            JsonObject response = AoeServices.starService.getStarTransactionById(id);
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
