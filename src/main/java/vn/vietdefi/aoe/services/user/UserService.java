package vn.vietdefi.aoe.services.user;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class UserService implements IUserService{
    @Override
    public JsonObject createUserProfile(long userId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject response = ApiServices.authService.get(userId);
            if(!BaseResponse.isSuccessFullMessage(response)){
                return response;
            }else{
                String username = response.getAsJsonObject("data").get("username").getAsString();
                JsonObject data = new JsonObject();
                data.addProperty("user_id", userId);
                data.addProperty("user_name", username);
                data.addProperty("nick_name", userId);
                bridge.update("aoe_profile", data);
                return BaseResponse.createFullMessageResponse(0, "success", data);
            }
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getUserProfile(long userId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_profile WHERE user_id = ?";
            JsonObject data = bridge.queryOne(query, userId);
            if(data == null){
                return createUserProfile(userId);
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateUserProfile(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long userId = data.get("userId").getAsLong();
            String query = "SELECT id FROM aoe_profile WHERE id = ?";
            if(bridge.queryExist(query, userId)){
                bridge.updateObjectToDb("aoe_profile", data);
                return BaseResponse.createFullMessageResponse(0, "success", data);
            }else{
                return BaseResponse.createFullMessageResponse(10, "user_not_found");
            }
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
