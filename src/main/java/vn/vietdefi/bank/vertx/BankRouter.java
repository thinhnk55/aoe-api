package vn.vietdefi.bank.vertx;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.BankServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class BankRouter {
    public static void timoLogin(RoutingContext routingContext) {
        try {
            String request = routingContext.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            String username = data.get("username").getAsString();
            String password = data.get("password").getAsString();
            JsonObject response = BankServices.timoService.loginTimo(username, password);
            routingContext.response().end(response.toString());
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1, "system_error");
            routingContext.response().end(response.toString());
        }
    }
    public static void timoCommit(RoutingContext routingContext) {
        try {
            String request = routingContext.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            long id = Long.parseLong(data.get("id").getAsString());
            String otp = data.get("otp").getAsString();
            JsonObject response = BankServices.timoService.commitTimo(id, otp);
            routingContext.response().end(response.toString());
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1, "system_error");
            routingContext.response().end(response.toString());
        }
    }
}
