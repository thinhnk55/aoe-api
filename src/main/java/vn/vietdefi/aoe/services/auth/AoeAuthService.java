package vn.vietdefi.aoe.services.auth;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.api.services.auth.UserConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class AoeAuthService implements IAoeAuthService{
    @Override
    public JsonObject register(String username, String password, int role, int status) {
        try{
            JsonObject response = ApiServices.authService.register(username,
                    password, role, status);
            if(BaseResponse.isSuccessFullMessage(response)){
                JsonObject user = response.getAsJsonObject("data");
                long userId = user.get("id").getAsLong();
                response = AoeServices.profileService.getUserProfileByUserId(userId);
                if(BaseResponse.isSuccessFullMessage(response)){
                    JsonObject profile = response.getAsJsonObject("data");
                    user.add("profile", profile);
                }
                response = AoeServices.starService.getStarWalletByUserId(userId);
                if(BaseResponse.isSuccessFullMessage(response)){
                    JsonObject star = response.getAsJsonObject("data");
                    user.add("star", star);
                }
                return BaseResponse.createFullMessageResponse(0, "success", user);
            }else{
                return response;
            }
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject register(JsonObject data) {
        try{
            String username = data.get("username").getAsString();
            String password = data.get("password").getAsString();
            return register(username,
                    password, UserConstant.ROLE_USER, UserConstant.STATUS_NORMAL);
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject login(JsonObject data) {
        try{
            String username = data.get("username").getAsString();
            String password = data.get("password").getAsString();
            return ApiServices.authService.login(username,
                    password);
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    /*These function user for TEST only. In real situation these actions is prohibited*/
    @Override
    public JsonObject deleteUser(long userId) {
        try{
            JsonObject response = ApiServices.authService.delete(userId);
            if(BaseResponse.isSuccessFullMessage(response)) {
                response = AoeServices.profileService.deleteProfile(userId);
                if(!BaseResponse.isSuccessFullMessage(response)){
                    return response;
                }
                response = AoeServices.starService.deleteStarWallet(userId);
                if(!BaseResponse.isSuccessFullMessage(response)){
                    return response;
                }
                response = AoeServices.donateService.deleteDonateBySenderId(userId);
                if (!BaseResponse.isSuccessFullMessage(response)) {
                    return response;
                }
            }
            return BaseResponse.createFullMessageResponse(0, "success");
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject changeStatus(long userId, int status) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE user SET status = ? WHERE id = ?";
            int x = bridge.update(query, status, userId);
            if(x == 1){
                return BaseResponse.createFullMessageResponse(0, "success");
            }else{
                return BaseResponse.createFullMessageResponse(10, "update_failure");
            }
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject get(String username) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM user WHERE username = ?";
            JsonObject data = bridge.queryOne(query, username);
            if(data == null){
                return BaseResponse.createFullMessageResponse(10, "user_not_exist");
            }
            data.remove("password");
            JsonObject response = AoeServices.profileService.getUserProfileByUserId(data.get("id").getAsLong());
            if(BaseResponse.isSuccessFullMessage(response)){
                data.add("profile", response.getAsJsonObject("data"));
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }catch (Exception e){
            e.printStackTrace();
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
