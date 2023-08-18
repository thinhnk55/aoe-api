package vn.vietdefi.aoe.services.user;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.api.services.auth.UserConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;
import vn.vietdefi.util.string.StringUtil;

public class UserService implements IUserService{
    @Override
    public boolean isExistPhone(String phoneNumber) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT id FROM user WHERE username = ?";
            return bridge.queryExist(query, phoneNumber);
        }catch (Exception exception){
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return false;
        }
    }

    @Override
    public JsonObject lockUser(long userId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE user SET status = ? WHERE id = ? AND status = ?";
            int row = bridge.update(query, UserConstant.STATUS_LOCKED, userId, UserConstant.STATUS_NORMAL);
            if(row == 0){
                return BaseResponse.createFullMessageResponse(10,"user_not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject unLockUser(long userId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE user SET status = ? WHERE id = ? AND status = ?";
            int row = bridge.update(query,UserConstant.STATUS_NORMAL, userId, UserConstant.STATUS_LOCKED);
            if(row == 0){
                return BaseResponse.createFullMessageResponse(10,"user_not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject forgotPassword(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            //get data
            String phoneNumber = data.get("phoneNumber").getAsString();
            String newPassword = data.get("newPassword").getAsString();
            int otp = data.get("otp").getAsInt();
            //check phone number
            String query = "SELECT id FROM user WHERE username = ?";
            long userId = bridge.queryLong(query, phoneNumber);
            if (userId == 0) return BaseResponse.createFullMessageResponse(14, "phone_not_found");
            //check otp
            JsonObject responseOtp = ApiServices.otpService.authorizeRandomOTP(userId, otp);
            if (!BaseResponse.isSuccessFullMessage(responseOtp)) {
                return responseOtp;
            }
            //forgot password
            String hashPassword = StringUtil.sha256(newPassword);
            query = "UPDATE user SET password = ? WHERE id = ?";
            bridge.update(query, hashPassword, userId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
