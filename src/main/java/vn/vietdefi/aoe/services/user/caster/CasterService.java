package vn.vietdefi.aoe.services.user.caster;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
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
            JsonObject response =
                    ApiServices.authService.get(phone);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                String password = StringUtil.generateRandomStringNumberCharacter(8);
                response = AoeServices.aoeAuthService
                        .register(phone, password, UserConstant.ROLE_USER,
                                UserConstant.STATUS_ACCOUNT_GENERATE);
                if (!BaseResponse.isSuccessFullMessage(response)) {
                    return response;
                }
            } else {
                return BaseResponse.createFullMessageResponse(10, "phone_exist");
            }
            JsonObject user = response.getAsJsonObject("data");
            data.addProperty("user_id", user.get("id").getAsLong());
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            bridge.insertObjectToDB("aoe_caster","user_id", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
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
    public JsonObject deleteCaster(long casterId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "DELETE FROM aoe_caster WHERE user_id = ?";
            int row = bridge.update(query, casterId);
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
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
