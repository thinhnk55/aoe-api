package vn.vietdefi.aoe.services.user.donor;

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

public class DonorService implements IDonorService {
    @Override
    public JsonObject createDonor(JsonObject data) {
        try {
            String phone = data.get("phone").getAsString();
            JsonObject response = ApiServices.authService.get(phone);
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
            bridge.insertObjectToDB("aoe_donor", "user_id", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateDonor(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            int row = bridge.updateObjectToDb("aoe_donor", "user_id", data);
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
    public JsonObject getDonorByUserId(long userId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_donor WHERE user_id =?";
            JsonObject data = bridge.queryOne(query, userId);
            if (data == null) {
                return BaseResponse.createFullMessageResponse(10, "donor_not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }


    public JsonObject getListDonor(long page, long recordPerPage) {
        try {
            long offset = (page - 1) * recordPerPage;
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_donor LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, recordPerPage, offset);
            JsonObject result = new JsonObject();
            result.add("donor", data);
            return BaseResponse.createFullMessageResponse(0, "success", result);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    @Override
    public JsonObject deleteDonorByUserId(long userId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "DELETE FROM aoe_donor WHERE user_id = ?";
            int row = bridge.update(query, userId);
            if (row == 0) {
                return BaseResponse.createFullMessageResponse(10, "delete_failure");
            } else {
                return BaseResponse.createFullMessageResponse(0, "success");
            }
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
