package vn.vietdefi.aoe.vertx.router.user;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class UserRouter {
    public static void lockUser(RoutingContext routingContext) {
        try {
            long userId = Long.parseLong(routingContext.request().getParam("id"));
            JsonObject response = AoeServices.userService.lockUser(userId);
            routingContext.response().end(response.toString());
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1, "system_error");
            routingContext.response().end(response.toString());
        }
    }

    public static void unLockUser(RoutingContext routingContext) {
        try {
            long userId = Long.parseLong(routingContext.request().getParam("id"));
            JsonObject response = AoeServices.userService.unLockUser(userId);
            routingContext.response().end(response.toString());
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1, "system_error");
            routingContext.response().end(response.toString());
        }
    }

    public static void changePassword(RoutingContext rc) {
        try {
            long userId = Long.parseLong(rc.request().getHeader("userid"));
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            JsonObject response = ApiServices.authService.changePassword(json, userId);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }
}
