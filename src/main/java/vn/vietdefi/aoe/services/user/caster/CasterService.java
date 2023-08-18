package vn.vietdefi.aoe.services.user.caster;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.api.services.auth.UserConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;
import vn.vietdefi.util.string.StringUtil;

public class CasterService implements ICasterService {
    @Override
    public JsonObject createCaster(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String fullname = json.get("fullname").getAsString();
            String nickname = json.get("nick_name").getAsString();
            String query = "SELECT user_id FROM caster WHERE nick_name = ?";
            JsonObject caster = bridge.queryOne(query, nickname);
            if (caster != null) return BaseResponse.createFullMessageResponse(11, "nickname_used");
            String avatar = json.get("avatar").getAsString();
            String phone_number = json.get("phone_number").getAsString();
            long clanId = json.get("clan_id").getAsLong();
            query = "SELECT user_id FROM caster WHERE phone_number = ?";
            caster = bridge.queryOne(query, phone_number);
            if (caster != null) return BaseResponse.createFullMessageResponse(10, "phone_number_used");
            JsonObject detail = new JsonObject();
            detail.add("nationality", json.get("nationality"));
            detail.add("address", json.get("address"));
            detail.add("date_of_birth", json.get("date_of_birth"));
            detail.add("fanpage_link", json.get("fanpage_link"));
            detail.add("fgroup_link", json.get("fgroup_link"));
            detail.add("youtube_link", json.get("youtube_link"));
            detail.add("tiktok_link", json.get("tiktok_link"));
            detail.add("sport", json.get("sport"));
            JsonArray image = json.get("image").getAsJsonArray();
            //create information user
            JsonObject createUser = new JsonObject();
            createUser.addProperty("username", phone_number);
            query = "SELECT id FROM user WHERE username = ?";
            if (bridge.queryExist(query, phone_number))
                return BaseResponse.createFullMessageResponse(11, "phone_number_used");

            String password = StringUtil.generateRandomStringNumberCharacter(12);

            long userid = ApiServices.authService.register(phone_number, password, UserConstant.ROLE_USER,UserConstant.STATUS_NORMAL).get("data").getAsJsonObject().get("id").getAsLong();
            query = "INSERT INTO `caster`(user_id,fullname,nick_name,avatar,detail,phone_number,image,clan_id)VALUES(?,?,?,?,?,?,?,?)";
            bridge.update(query, userid, fullname, nickname, avatar, detail, phone_number, image, clanId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    @Override
    public JsonObject updateCaster(long casterId, JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String fullname = json.get("fullname").getAsString();
            String nickname = json.get("nickname").getAsString();
            String query = "SELECT user_id FROM caster WHERE nick_name = ?";
            JsonObject caster = bridge.queryOne(query, nickname);
            if (caster != null && caster.get("user_id").getAsInt() != casterId)
                return BaseResponse.createFullMessageResponse(11, "nickname_used");
            String avatar = json.get("avatar").getAsString();
            String phone_number = json.get("phone_number").getAsString();
            long clanId = json.get("clan_id").getAsLong();
            query = "SELECT user_id FROM caster WHERE phone_number = ?";
            caster = bridge.queryOne(query, phone_number);
            if (caster != null && caster.get("user_id").getAsInt() != casterId)
                return BaseResponse.createFullMessageResponse(10, "phone_number_used");
            JsonObject detail = new JsonObject();
            detail.add("address", json.get("address"));
            detail.add("date_of_birth", json.get("date_of_birth"));
            detail.add("fanpage_link", json.get("fanpage_link"));
            detail.add("fgroup_link", json.get("fgroup_link"));
            detail.add("youtube_link", json.get("youtube_link"));
            detail.add("tiktok_link", json.get("tiktok_link"));
            detail.add("sport", json.get("sport"));
            JsonArray image = json.get("image").getAsJsonArray();

            query = "UPDATE caster SET fullname = ?,nick_name = ?,avatar = ?, phone_number = ? ,detail = ?,image = ?,clan_id =? WHERE user_id =? ";
            if (bridge.update(query, fullname, nickname, avatar, phone_number, detail, image,clanId, casterId) == 0) {
                return BaseResponse.createFullMessageResponse(10, "nothing_to_update");
            }
            return BaseResponse.createFullMessageResponse(0, "success");
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
            String query = "UPDATE caster SET is_deleted = 1 WHERE user_id = ? AND is_deleted = 0 ";
            int row = bridge.update(query, casterId);
            if (row == 0) {
                return BaseResponse.createFullMessageResponse(10, "caster_not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getCasterByUserId(long user_id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM caster WHERE user_id = ?";
            JsonObject data = bridge.queryOne(query, user_id);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
