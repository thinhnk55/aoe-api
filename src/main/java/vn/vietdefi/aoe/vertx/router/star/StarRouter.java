package vn.vietdefi.aoe.vertx.router.star;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class StarRouter {
    public static void getStarWallet(RoutingContext rc) {
        try {
            long id = Long.parseLong(rc.request().getHeader("userid"));
            JsonObject response = AoeServices.starService.getStarWalletByUserId(id);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(
                    1, "system_error").toString());
        }
    }
    public static void adminGetStarWallet(RoutingContext rc){
        try {
            long userId = Long.parseLong(rc.request().getParam("user_id"));
            JsonObject response = AoeServices.starService.getStarWalletByUserId(userId);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(
                    1, "system_error").toString());
        }
    }


    public static void listByService(RoutingContext rc) {
        try {
            long userId = Long.parseLong(rc.request().getHeader("userid"));
            int service = Integer.parseInt(rc.request().getParam("service"));
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            JsonObject response = AoeServices.starService.listStarTransactionOfUserByService(userId, service, page, StarConstant.DEFAULT_RECORD_PER_PAGE);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(
                    1, "system_error").toString());
        }
    }

    public static void listByTime(RoutingContext rc) {
        try {
            long userId = Long.parseLong(rc.request().getHeader("userid"));
            long from = Long.parseLong(rc.request().getParam("from"));
            long to = Long.parseLong(rc.request().getParam("to"));
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            if(from != 0) {
                JsonObject response = AoeServices.starService
                        .listStarTransactionOfUserByTime(userId, from, to,
                                page, StarConstant.DEFAULT_RECORD_PER_PAGE);
                rc.response().end(response.toString());
            }else{
                JsonObject response = AoeServices.starService
                        .listStarTransactionOfUserAll(userId,
                                page, StarConstant.DEFAULT_RECORD_PER_PAGE);
                rc.response().end(response.toString());
            }
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(
                    1, "system_error").toString());
        }
    }

    public static void adminListOfUserByTime(RoutingContext rc) {
        try {
            long userId = Long.parseLong(rc.request().getParam("user_id"));
            long from = Long.parseLong(rc.request().getParam("from"));
            long to = Long.parseLong(rc.request().getParam("to"));
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            JsonObject response = AoeServices.starService
                    .listStarTransactionOfUserByTime(userId, from, to, page, StarConstant.DEFAULT_RECORD_PER_PAGE);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(
                    1, "system_error").toString());
        }
    }

    public static void systemAdminExchangeStar(RoutingContext rc) {
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            long userId = json.get("user_id").getAsLong();
            int service = json.get("service").getAsInt();
            long amount = json.get("amount").getAsLong();
            long referId = json.get("referId").getAsLong();
            JsonObject response = AoeServices.starService
                    .exchangeStar(userId, service, amount, referId);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(
                    1, "system_error").toString());
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

    public static void lookupRechargeHistory(RoutingContext rc) {
        try {
            String phoneNumber = rc.request().getParam("phone_number", "");
            long from = Long.parseLong(rc.request().getParam("from", "0"));
            long to = Long.parseLong(rc.request().getParam("to", "0"));
            long page = Long.parseLong(rc.request().getParam("page", "1"));
            JsonObject response = AoeServices.starService.lookupRechargeHistory(phoneNumber, from, to, page, StarConstant.DEFAULT_RECORD_PER_PAGE);
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
