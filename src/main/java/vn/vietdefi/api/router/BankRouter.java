package vn.vietdefi.api.router;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.ApiBank;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class BankRouter {
    public static void login(RoutingContext routingContext) {
        try {
            String request = routingContext.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            JsonObject response = ApiBank.bankService.login(data);
            routingContext.response().end(response.toString());
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1, "system_error");
            routingContext.response().end(response.toString());
        }
    }
    public static void commit(RoutingContext routingContext) {
        try {
            String request = routingContext.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            JsonObject response = ApiBank.bankService.commit(data);
            routingContext.response().end(response.toString());
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1, "system_error");
            routingContext.response().end(response.toString());
        }
    }
}
