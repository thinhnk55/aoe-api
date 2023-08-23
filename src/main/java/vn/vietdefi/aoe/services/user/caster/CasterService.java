package vn.vietdefi.aoe.services.user.caster;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.vertx.core.json.Json;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.api.services.auth.UserConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;
import vn.vietdefi.util.string.StringUtil;

public class CasterService implements ICasterService {
    @Override
    public JsonObject createCaster(JsonObject data) {
        try {
            String phone = data.get("phone").getAsString();
            String nickname = data.get("nick_name").getAsString();
            JsonObject response = getCasterByNickname(nickname);
            if (BaseResponse.isSuccessFullMessage(response)) {
                return BaseResponse.createFullMessageResponse(13, "nick_name_exists");
            }
            response = ApiServices.authService.get(phone);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                String password = StringUtil.generateRandomStringNumberCharacter(8);
                response = AoeServices.aoeAuthService.register(phone, password, UserConstant.ROLE_USER, UserConstant.STATUS_ACCOUNT_GENERATE);
                if (!BaseResponse.isSuccessFullMessage(response)) {
                    return response;
                }
            } else {
                long userId = response.getAsJsonObject("data").get("id").getAsLong();
                String password = StringUtil.generateRandomStringNumberCharacter(8);
                ApiServices.authService.updatePassword(userId, password);
            }
            JsonObject user = response.getAsJsonObject("data");
            data.addProperty("user_id", user.get("id").getAsLong());
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            bridge.insertObjectToDB("aoe_caster", "user_id", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    private JsonObject getCasterByNickname(String nickname) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT user_id FROM aoe_caster WHERE nick_name = ?";
            JsonObject user = bridge.queryOne(query, nickname);
            if (user == null) {
                return BaseResponse.createFullMessageResponse(11, "caster_not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success", user);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateCaster(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            int row = bridge.updateObjectToDb("aoe_caster", "user_id", data);
            if (row == 0) {
                return BaseResponse.createFullMessageResponse(10, "update_failure");
            } else {
                return BaseResponse.createFullMessageResponse(0, "success");
            }
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject deleteCaster(String nickname) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "DELETE FROM aoe_caster WHERE nick_name = ?";
            int row = bridge.update(query, nickname);
            if (row == 0) {
                return BaseResponse.createFullMessageResponse(10, "delete_failure");
            }
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getCasterByUserId(long casterId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_caster WHERE user_id = ?";
            JsonObject data = bridge.queryOne(query, casterId);
            if (data == null) {
                return BaseResponse.createFullMessageResponse(10, "caster_not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listCaster(long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            String query = "SELECT * FROM aoe_caster LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, recordPerPage, offset);
            JsonObject result = new JsonObject();
            result.add("caster", data);
            return BaseResponse.createFullMessageResponse(0, "success", result);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject casterUpdateStatistic(long id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject response = AoeServices.donateService.statisticDonate(StarConstant.SERVICE_DONATE_CASTER, id);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return response;
            }
            JsonObject data = response.getAsJsonObject("data");
            String query = "UPDATE aoe_caster SET total_star_donate = ?, total_supporter = ?  WHERE user_id = ?";
            int x = bridge.update(query, data.get("total_star_donate").getAsLong(), data.get("total_supporter").getAsLong(), id);
            if (x == 1) return BaseResponse.createFullMessageResponse(0, "success");
            return BaseResponse.createFullMessageResponse(10, "not_found");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
