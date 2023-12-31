package vn.vietdefi.api.vertx.router;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.api.services.auth.UserConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class AuthRouter {
    public static void authorizeUser(RoutingContext rc) {
        try {
            long userid = Long.parseLong(rc.request().getHeader("userid"));
            String token = rc.request().getHeader("token");
            JsonObject response = ApiServices.authService.authorize(userid, token);
            if (BaseResponse.isSuccessFullMessage(response)) {
                JsonObject user = response.getAsJsonObject("data");
                int role = user.get("role").getAsInt();
                if (role >= UserConstant.ROLE_USER) {
                    rc.request().headers().add("username", user.get("username").getAsString());
                    rc.request().headers().add("role", user.get("role").getAsString());
                    rc.next();
                } else {
                    response = BaseResponse.createFullMessageResponse(
                            2, "unauthorized"
                    );
                    rc.response().end(response.toString());
                }
            } else {
                response = BaseResponse.createFullMessageResponse(
                        2, "unauthorized"
                );
                rc.response().end(response.toString());
            }
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }
    public static void authorizeSuperAdmin(RoutingContext rc) {
        try {
            long userid = Long.parseLong(rc.request().getHeader("userid"));
            String token = rc.request().getHeader("token");
            JsonObject response = ApiServices.authService.authorize(userid, token);
            if (BaseResponse.isSuccessFullMessage(response)) {
                JsonObject user = response.getAsJsonObject("data");
                int role = user.get("role").getAsInt();
                if (role >= UserConstant.ROLE_SUPER_ADMIN) {
                    rc.request().headers().add("username", user.get("username").getAsString());
                    rc.request().headers().add("role", user.get("role").getAsString());
                    rc.next();
                } else {
                    response = BaseResponse.createFullMessageResponse(
                            2, "unauthorized"
                    );
                    rc.response().end(response.toString());
                }
            } else {
                response = BaseResponse.createFullMessageResponse(
                        2, "unauthorized"
                );
                rc.response().end(response.toString());
            }
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }
    public static void authorizeSystemAdmin(RoutingContext rc) {
        try {
            long userid = Long.parseLong(rc.request().getHeader("userid"));
            String token = rc.request().getHeader("token");
            JsonObject response = ApiServices.authService.authorize(userid, token);
            if (BaseResponse.isSuccessFullMessage(response)) {
                JsonObject user = response.getAsJsonObject("data");
                int role = user.get("role").getAsInt();
                if (role >= UserConstant.ROLE_SYSTEM_ADMIN) {
                    rc.request().headers().add("username", user.get("username").getAsString());
                    rc.request().headers().add("role", user.get("role").getAsString());
                    rc.next();
                } else {
                    response = BaseResponse.createFullMessageResponse(
                            2, "unauthorized"
                    );
                    rc.response().end(response.toString());
                }
            } else {
                response = BaseResponse.createFullMessageResponse(
                        2, "unauthorized"
                );
                rc.response().end(response.toString());
            }
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }
    public static void authorizeAdmin(RoutingContext rc) {
        try {
            long userid = Long.parseLong(rc.request().getHeader("userid"));
            String token = rc.request().getHeader("token");
            JsonObject response = ApiServices.authService.authorize(userid, token);
            if (BaseResponse.isSuccessFullMessage(response)) {
                JsonObject user = response.getAsJsonObject("data");
                int role = user.get("role").getAsInt();
                if (role >= UserConstant.ROLE_ADMIN) {
                    rc.request().headers().add("username", user.get("username").getAsString());
                    rc.request().headers().add("role", user.get("role").getAsString());
                    rc.next();
                } else {
                    response = BaseResponse.createFullMessageResponse(
                            2, "unauthorized"
                    );
                    rc.response().end(response.toString());
                }
            } else {
                response = BaseResponse.createFullMessageResponse(
                        2, "unauthorized"
                );
                rc.response().end(response.toString());
            }
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }
    public static void authorizeSupport(RoutingContext rc) {
        try {
            long userid = Long.parseLong(rc.request().getHeader("userid"));
            String token = rc.request().getHeader("token");
            JsonObject response = ApiServices.authService.authorize(userid, token);
            if (BaseResponse.isSuccessFullMessage(response)) {
                JsonObject user = response.getAsJsonObject("data");
                int role = user.get("role").getAsInt();
                if (role >= UserConstant.ROLE_SUPPORT) {
                    rc.request().headers().add("username", user.get("username").getAsString());
                    rc.request().headers().add("role", user.get("role").getAsString());
                    rc.next();
                } else {
                    response = BaseResponse.createFullMessageResponse(
                            2, "unauthorized"
                    );
                    rc.response().end(response.toString());
                }
            } else {
                response = BaseResponse.createFullMessageResponse(
                        2, "unauthorized"
                );
                rc.response().end(response.toString());
            }
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }
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

    public static void updateUserId(RoutingContext rc) {
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            long userId = Long.parseLong(json.get("user_id").getAsString());
            long newUserId = Long.parseLong(json.get("user_id").getAsString());
            JsonObject response = ApiServices.authService.updateUserId(userId, newUserId);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }
    public static void updateUsername(RoutingContext rc) {
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            long userId = Long.parseLong(json.get("user_id").getAsString());
            String username = json.get("username").getAsString();
            JsonObject response = ApiServices.authService.updateUsername(userId, username);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }
    public static void deleteUser(RoutingContext rc) {
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            long userId = Long.parseLong(json.get("user_id").getAsString());
            JsonObject response = ApiServices.authService.delete(userId);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }
    public static void adminGetUserByUserName(RoutingContext rc) {
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            String username = json.get("username").getAsString();
            JsonObject response = ApiServices.authService.get(username);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }
    public static void adminGetUserByUserId(RoutingContext rc) {
        try {
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            String userId = json.get("user_id").getAsString();
            JsonObject response = ApiServices.authService.get(userId);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }

    public static void logout(RoutingContext rc) {
        try {
            long userId = Long.parseLong(rc.request().getHeader("userid"));
            JsonObject response = ApiServices.authService.logout(userId);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }


}
