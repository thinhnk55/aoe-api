package vn.vietdefi.aoe.services.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

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
                data.addProperty("nick_name", username);
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
    public JsonObject updateUserProfile(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long userId = data.get("user_id").getAsLong();
            String query = "SELECT * FROM aoe_profile WHERE user_id = ?";
            if (bridge.queryExist(query, userId)) {
                bridge.updateObjectToDb("aoe_profile", "user_id", data);
                return BaseResponse.createFullMessageResponse(0, "success", data);
            } else {
                return BaseResponse.createFullMessageResponse(10, "user_not_found");
            }
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject searchProfile(JsonObject data) {
        try {
            String username = data.get("username").getAsString();
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM aoe_profile WHERE username LIKE '%")
                    .append(username).append("%' LIMIT 10");
            JsonArray results = bridge.query(query.toString());
            return BaseResponse.createFullMessageResponse(0, "success", results);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getUserProfile(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long userid = data.get("user_id").getAsLong();
            String query = "SELECT * FROM aoe_profile WHERE user_id = ?";
            JsonObject user = bridge.queryOne(query, userid);
            if (user == null) {
                return createUserProfile(userid);
            }
            data.addProperty("username",user.get("username").getAsString());
            data.addProperty("nick_name",user.get("nick_name").getAsString());
            data.addProperty("avatar",user.get("avatar").getAsString());

            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
