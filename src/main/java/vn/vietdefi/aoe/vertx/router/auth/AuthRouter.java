package vn.vietdefi.aoe.vertx.router.auth;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.api.services.auth.UserConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class AuthRouter {
    public static void register(RoutingContext rc) {
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            String username = json.get("username").getAsString();
            String password = json.get("password").getAsString();
            JsonObject response = ApiServices.authService.register(username,
                    password, UserConstant.ROLE_USER, UserConstant.STATUS_NORMAL);
            rc.response().end(response.toString());
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }
    public static void login(RoutingContext rc) {
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            String username = json.get("username").getAsString();
            String password = json.get("password").getAsString();
            JsonObject response = ApiServices.authService.login(username, password);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }
    public static void logout(RoutingContext rc) {
        try {
            long userid = Long.parseLong(rc.request().getHeader("userid"));
            JsonObject response = ApiServices.authService.logout(userid);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }
}
