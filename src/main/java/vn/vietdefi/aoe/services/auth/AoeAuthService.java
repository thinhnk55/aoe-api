package vn.vietdefi.aoe.services.auth;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.api.services.auth.UserConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;

public class AoeAuthService implements IAoeAuthService{
    @Override
    public JsonObject register(JsonObject data) {
        try{
            String username = data.get("username").getAsString();
            String password = data.get("password").getAsString();
            JsonObject response = ApiServices.authService.register(username,
                    password, UserConstant.ROLE_USER, UserConstant.STATUS_NORMAL);
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
            }
            return BaseResponse.createFullMessageResponse(0, "success");
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
