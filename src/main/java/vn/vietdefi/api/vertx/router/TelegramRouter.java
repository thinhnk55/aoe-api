package vn.vietdefi.api.vertx.router;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;

public class TelegramRouter {
    public static void requestLinkAccount(RoutingContext rc) {
        try {
            long userid = Long.parseLong(rc.request().getHeader("userid"));
            JsonObject data = new JsonObject();
            data.addProperty("userid", userid);
            JsonObject response = ApiServices.telegramService.requestLinkAccount(data);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1, "system_error");
            rc.response().end(response.toString());
        }
    }
}
