package vn.vietdefi.aoe.vertx.router.monitor;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.monitor.MonitorController;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;

public class MonitorRouter {
    public static void getMonitor(RoutingContext rc) {
        try {
            JsonObject response = MonitorController.instance().getInfo();
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
