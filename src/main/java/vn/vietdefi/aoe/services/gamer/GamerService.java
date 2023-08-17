package vn.vietdefi.aoe.services.gamer;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.api.services.auth.UserConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;
import vn.vietdefi.util.string.StringUtil;

public class GamerService implements IGamerService {
    public JsonObject create(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();

            String nickname = json.get("nickname").getAsString();

            String query = "SELECT user_id FROM gamer WHERE nickname = ?";
            Long existingUserid = bridge.queryLong(query, nickname);
            if (existingUserid != null) {
                return BaseResponse.createFullMessageResponse(10, "nickname_exist");
            }

            String mainName = json.get("main_name").getAsString();
            String avatar = json.get("avatar").getAsString();
            int matchPlayed = json.get("match_played").getAsInt();
            long clanId = json.get("clan_id").getAsLong();
            int rank = json.get("rank").getAsInt();
            JsonObject rankInfo = json.get("rank_info").getAsJsonObject();
            int matchWon = json.get("match_won").getAsInt();
            String phoneNumber = json.get("phone_number").getAsString();


            JsonObject info = new JsonObject();

            info.addProperty("date_of_birth", json.get("date_of_birth").getAsLong());
            info.addProperty("address", json.get("address").getAsString());
            info.addProperty("sport", json.get("sport").getAsString());
            info.addProperty("nationality", json.get("nationality").getAsString());
            info.addProperty("facebook_link", json.get("facebook_link").getAsString());
            info.addProperty("tiktok_link", json.get("tiktok_link").getAsString());
            info.addProperty("youtube_link", json.get("youtube_link").getAsString());
            info.addProperty("fanpage_link", json.get("fanpage_link").getAsString());
            info.addProperty("telegram_link", json.get("telegram_link").getAsString());
            info.add("image", json.get("image").getAsJsonArray());

            long createTime = System.currentTimeMillis();


            query = "SELECT id FROM user WHERE username = ?";
            if (bridge.queryExist(query, phoneNumber))
                return BaseResponse.createFullMessageResponse(11, "phone_number_used");
            String password = StringUtil.generateRandomStringNumberCharacter(12);

            long userid = ApiServices.authService.register(phoneNumber, password, UserConstant.ROLE_USER,UserConstant.STATUS_NORMAL).get("data").getAsJsonObject().get("id").getAsLong();
            query = "INSERT INTO gamer (user_id, nickname,main_name,avatar,detail_info,clan_id, rank,rank_info,match_played,match_won,update_time,status,phone_number) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
            bridge.update(query, userid, nickname, mainName, avatar, info, clanId, rank, rankInfo, matchPlayed, matchWon, createTime, 0, phoneNumber);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stackTrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    public JsonObject updateInfo(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long userId = json.get("userid").getAsLong();
            String query = "SELECT user_id FROM gamer WHERE user_id = ? and status = ?";
            if (!bridge.queryExist(query, userId, 0)) {
                return BaseResponse.createFullMessageResponse(11, "gamer_not_exist");
            }
            String nickname = json.get("nickname").getAsString();
            query = "SELECT user_id FROM gamer WHERE `nickname` = ?";
            JsonObject user = bridge.queryOne(query, nickname);
            if (user != null && user.get("user_id").getAsLong() != userId)
                return BaseResponse.createFullMessageResponse(10, "nickname_exist");

            String mainName = json.get("main_name").getAsString();
            String avatar = json.get("avatar").getAsString();
            int matchPlayed = json.get("match_played").getAsInt();
            int rank = json.get("rank").getAsInt();
            int matchWon = json.get("match_won").getAsInt();
            long updateTime = System.currentTimeMillis();
            long clanId = json.get("clan_id").getAsLong();
            JsonObject rankInfo = json.get("rank_info").getAsJsonObject();

            JsonObject detailInfo = new JsonObject();

            detailInfo.addProperty("date_of_birth", json.get("date_of_birth").getAsLong());
            detailInfo.addProperty("address", json.get("address").getAsString());
            detailInfo.addProperty("sport", json.get("sport").getAsString());
            detailInfo.addProperty("nationality", json.get("nationality").getAsString());
            detailInfo.addProperty("facebook_link", json.get("facebook_link").getAsString());
            detailInfo.addProperty("tiktok_link", json.get("tiktok_link").getAsString());
            detailInfo.addProperty("youtube_link", json.get("youtube_link").getAsString());
            detailInfo.addProperty("fanpage_link", json.get("fanpage_link").getAsString());
            detailInfo.addProperty("telegram_link", json.get("telegram_link").getAsString());
            detailInfo.add("image", json.get("image"));


            String updateQuery = "UPDATE gamer SET nickname = ?, main_name = ?, avatar = ?, detail_info = ?, " +
                    "clan_id = ?, rank = ?, rank_info = ?, match_played = ?, match_won = ? " +
                    ", update_time = ?,status = ? WHERE user_id = ?";

            bridge.update(updateQuery, nickname, mainName, avatar, detailInfo, clanId, rank,
                    rankInfo, matchPlayed, matchWon, updateTime, 0, userId);

            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stackTrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject deleteAccountant(long userid) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT status FROM gamer WHERE user_id = ?";
            Integer status = bridge.queryInteger(query, userid);
            if (status == null) {
                return BaseResponse.createFullMessageResponse(11, "gamer_not_exist");
            } else if (status == 1) {
                return BaseResponse.createFullMessageResponse(13, "accountant_deleted");
            } else {
                query = "UPDATE gamer SET status = 1 WHERE user_id = ?";
                bridge.update(query, userid);
            }
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
