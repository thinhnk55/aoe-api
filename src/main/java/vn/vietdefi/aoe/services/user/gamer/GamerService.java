package vn.vietdefi.aoe.services.user.gamer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.match.MatchConstants;
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

            String nickname = json.get("nick_name").getAsString();
            String phone = json.get("phone").getAsString();

            String query = "SELECT user_id FROM gamer WHERE nick_name = ?";
            Long existingUserid = bridge.queryLong(query, nickname);
            if (existingUserid != null) {
                return BaseResponse.createFullMessageResponse(10, "nick_name_exist");
            }
            long createTime = System.currentTimeMillis();
            json.addProperty("update_time", createTime);
            query = "SELECT id FROM user WHERE username = ?";
            if (bridge.queryExist(query,phone ))
                return BaseResponse.createFullMessageResponse(11, "nickname_exist");
//            String password = StringUtil.generateRandomStringNumberCharacter(12);
            String password = "password";
            long userid = ApiServices.authService.register(phone, password, UserConstant.ROLE_USER,UserConstant.STATUS_NORMAL).get("data").getAsJsonObject().get("id").getAsLong();
            json.addProperty("user_id", userid);
            bridge.insertObjectToDB("gamer","user_id",json);

//            JsonObject info = new JsonObject();

//            info.addProperty("date_of_birth", json.get("date_of_birth").getAsLong());
//            info.addProperty("address", json.get("address").getAsString());
//            info.addProperty("sport", json.get("sport").getAsString());
//            info.addProperty("nationality", json.get("nationality").getAsString());
//            info.addProperty("facebook_link", json.get("facebook_link").getAsString());
//            info.addProperty("tiktok_link", json.get("tiktok_link").getAsString());
//            info.addProperty("youtube_link", json.get("youtube_link").getAsString());
//            info.addProperty("fanpage_link", json.get("fanpage_link").getAsString());
//            info.addProperty("telegram_link", json.get("telegram_link").getAsString());
//            info.add("image", json.get("image").getAsJsonArray());


            return BaseResponse.createFullMessageResponse(0, "success",json);
        } catch (Exception e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stackTrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getGamerByUserId(long id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM gamer WHERE user_id = ? AND status = ?";
            JsonObject data = bridge.queryOne(query, id, 0);
            if (data == null) {
                return BaseResponse.createFullMessageResponse(11, "gamer_not_exist");
            }
            long clanId = data.get("clan_id").getAsLong();
            data.remove("username");
            if(clanId != 0) {
                JsonObject clan = AoeServices.clanService.getClanById(clanId);
                data.add("clan_name", clan.get("clan_name"));
            }
            AoeServices.donateService.getTotalDonateByUserId(id);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
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
            String nickname = json.get("nick_name").getAsString();
            query = "SELECT user_id FROM gamer WHERE nick_name = ?";
            JsonObject user = bridge.queryOne(query, nickname);
            if (user != null && user.get("user_id").getAsLong() != userId)
                return BaseResponse.createFullMessageResponse(10, "nickname_exist");

            String mainName = json.get("fullname").getAsString();
            String avatar = json.get("avatar").getAsString();
            int matchPlayed = json.get("match_played").getAsInt();
            int rank = json.get("rank").getAsInt();
            int matchWon = json.get("match_won").getAsInt();
            long updateTime = System.currentTimeMillis();
            long clanId = json.get("clan_id").getAsLong();
            JsonObject rankInfo = json.get("rank_info").getAsJsonObject();

            JsonObject detailInfo = new JsonObject();

            detailInfo.addProperty("date_of_birth", json.get("date_of_birth").getAsString());
            detailInfo.addProperty("address", json.get("address").getAsString());
            detailInfo.addProperty("sport", json.get("sport").getAsString());
            detailInfo.addProperty("nationality", json.get("nationality").getAsString());
            detailInfo.addProperty("facebook_link", json.get("facebook_link").getAsString());
            detailInfo.addProperty("tiktok_link", json.get("tiktok_link").getAsString());
            detailInfo.addProperty("youtube_link", json.get("youtube_link").getAsString());
            detailInfo.addProperty("fanpage_link", json.get("fanpage_link").getAsString());
            detailInfo.addProperty("telegram_link", json.get("telegram_link").getAsString());
            detailInfo.add("image", json.get("image"));


            StringBuilder updateQuery = new StringBuilder("UPDATE gamer SET nick_name = ?, fullname = ?, avatar = ?, detail_info = ?, ") ;
                    updateQuery.append("clan_id = ?, rank = ?, rank_info = ?, match_played = ?, match_won = ? ");
                    updateQuery.append(", update_time = ?,status = ? WHERE user_id = ?");

            bridge.update(updateQuery.toString(), nickname, mainName, avatar, detailInfo, clanId, rank,
                    rankInfo, matchPlayed, matchWon, updateTime, 0, userId);

            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stackTrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listGamerByMatchId(long matchId) {
        try {
            JsonObject response = AoeServices.matchService.getById(matchId);
            if(!BaseResponse.isSuccessFullMessage(response)) {
                return response;
            }
            JsonArray gamers = response.getAsJsonObject("data").get("detail").getAsJsonArray();
            return null;
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listGamer(long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject result = new JsonObject();
            long offset = (page - 1) * recordPerPage;
            String countQuery = "SELECT COUNT(*) AS total_rows FROM gamer";
            result.addProperty("total_page", bridge.queryInteger(countQuery) / recordPerPage + 1);
            String query = "SELECT * FROM gamer LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, recordPerPage, offset);
            result.add("gamer", data);
            return BaseResponse.createFullMessageResponse(0, "success", result);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listGamerOfClan(long id, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject result = new JsonObject();
            long offset = (page - 1) * recordPerPage;
            String countQuery = "SELECT COUNT(*) AS total_rows FROM gamer WHERE clan_id = ?";
            result.addProperty("total_page", bridge.queryInteger(countQuery, id) / recordPerPage + 1);
            String query = "SELECT * FROM gamer WHERE clan_id = ? LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query,id, recordPerPage, offset);
            result.add("gamer", data);
            return BaseResponse.createFullMessageResponse(0, "success", result);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listMatch(long id, long page, long recordPerPage) {
        try {
            JsonObject response = new JsonObject();
            JsonObject data = AoeServices.matchService.getListMatch(MatchConstants.STATE_VOTING, page, recordPerPage);
            JsonArray result = data.getAsJsonObject("data").getAsJsonArray("match");
            for(JsonElement element : result) {
                JsonArray teams = element.getAsJsonObject().getAsJsonArray("team_player");
                for(JsonElement team : teams) {
                    if(team.getAsJsonObject().get("gamer_id").getAsInt() == id){
                        response.add("team_player", team);
                    }
                }
            }
            return BaseResponse.createFullMessageResponse(0, "success", response);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
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
