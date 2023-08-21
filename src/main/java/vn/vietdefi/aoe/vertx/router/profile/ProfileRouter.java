package vn.vietdefi.aoe.vertx.router.profile;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class ProfileRouter {
    public static void getProfile(RoutingContext rc) {
        try {
            long id = Long.parseLong(rc.request().getHeader("userid"));
            JsonObject response = AoeServices.profileService.getUserProfileByUserId(id);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }
    public static void searchProfile(RoutingContext rc) {
        try {
            String query = rc.request().getParam("q");
            JsonObject response = AoeServices.profileService.searchProfile(query);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void updateProfile(RoutingContext rc) {
        try {
            long userId = Long.parseLong(rc.request().getHeader("userid"));
            String request = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            JsonObject response = AoeServices.profileService.updateUserProfile(userId, data);
            rc.response().end(response.toString());
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(
                    1, "system_error");
            rc.response().end(response.toString());
        }
    }

    public static void updateLanguage(RoutingContext rc) {
        try {
            long id = Long.parseLong(rc.request().getHeader("userId"));
            String request = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            int lang = data.get("lang").getAsInt();
            JsonObject response = AoeServices.profileService.updateLanguage(id, lang);
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
