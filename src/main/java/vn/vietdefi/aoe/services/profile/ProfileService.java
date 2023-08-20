package vn.vietdefi.aoe.services.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;
import vn.vietdefi.util.string.StringUtil;

public class ProfileService implements IProfileService {
    private JsonObject createUserProfile(long userId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject response = ApiServices.authService.get(userId);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return response;
            } else {
                String username = response.getAsJsonObject("data").get("username").getAsString();
                JsonObject data = new JsonObject();
                data.addProperty("user_id", userId);
                data.addProperty("username", username);
                data.addProperty("nick_name", StringUtil.addThreeStarsToPhoneNumber(username));
                data.addProperty("level", 0);
                String query = "INSERT INTO aoe_profile(user_id, username, nick_name) VALUES(?,?,?)";
                bridge.update(query, userId, username, username);
                return BaseResponse.createFullMessageResponse(0, "success", data);
            }
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getUserProfileByUserId(long userId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_profile WHERE user_id = ?";
            JsonObject data = bridge.queryOne(query, userId);
            if (data == null) {
                return createUserProfile(userId);
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateUserProfile(long userId, JsonObject data) {
        try{
            long user_id = data.get("user_id").getAsLong();
            if(user_id != userId){
                return BaseResponse.createFullMessageResponse(10, "update_reject");
            }
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            bridge.updateObjectToDb("aoe_profile", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject searchProfile(String username) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = new StringBuilder()
                    .append("SELECT * FROM aoe_profile WHERE username LIKE '%")
                    .append(username).append("%' LIMIT 10")
                    .toString();
            JsonArray results = bridge.query(query);
            return BaseResponse.createFullMessageResponse(0, "success", results);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateLanguage(long id, int lang) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE aoe_profile SET lang = ? WHERE user_id = ?";
            int row = bridge.update(query, lang, id);
            if(row == 0){
                return BaseResponse.createFullMessageResponse(10, "user_not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
