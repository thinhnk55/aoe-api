package vn.vietdefi.aoe.services.user.gamer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.api.services.auth.UserConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;
import vn.vietdefi.util.string.StringUtil;

public class GamerService implements IGamerService {
    public JsonObject create(JsonObject data) {
        try {
            String phone = data.get("phone").getAsString();
            String nickname = data.get("nick_name").getAsString();
            JsonObject response = getGamerByNickName(nickname);
            if (BaseResponse.isSuccessFullMessage(response)) {
                return BaseResponse.createFullMessageResponse(13, "nick_name_exists");
            }
            response = AoeServices.gamerService.getByPhone(phone);
            if (BaseResponse.isSuccessFullMessage(response)) {
                return BaseResponse.createFullMessageResponse(14, "phone_exists");
            }
            response = ApiServices.authService.get(phone);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                String password = StringUtil.generateRandomStringNumberCharacter(8);
                response = AoeServices.aoeAuthService.register(phone, password, UserConstant.ROLE_USER, UserConstant.STATUS_ACCOUNT_GENERATE);
                if (!BaseResponse.isSuccessFullMessage(response)) {
                    return response;
                }
            }
            long userId = response.getAsJsonObject("data").get("id").getAsLong();
            data.addProperty("user_id", userId);
            long createTime = System.currentTimeMillis();
            data.addProperty("create_time", createTime);
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            bridge.insertObjectToDB("aoe_gamer", "user_id", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stackTrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }


    public JsonObject getGamerByNickName(String nickname) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT user_id FROM aoe_gamer WHERE nick_name = ?";
            JsonObject user = bridge.queryOne(query, nickname);
            if (user == null) {
                return BaseResponse.createFullMessageResponse(11, "gamer_not_exist");
            }
            return BaseResponse.createFullMessageResponse(0, "success", user);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getGamerByUserId(long userId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_gamer WHERE user_id = ?";
            JsonObject data = bridge.queryOne(query, userId);
            if (data == null) {
                return BaseResponse.createFullMessageResponse(11, "gamer_not_exist");
            }
            long clanId = data.get("clan_id").getAsLong();
            if (clanId != 0) {
                JsonObject response = AoeServices.clanService.getClanById(clanId);
                if (BaseResponse.isSuccessFullMessage(response)) {
                    JsonObject clan = response.getAsJsonObject("data");
                    data.add("clan", clan);
                }
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getPartialGamer(long userId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT user_id, nick_name, full_name, avatar FROM aoe_gamer WHERE user_id = ?";
            JsonObject data = bridge.queryOne(query, userId);
            if (data == null) {
                return BaseResponse.createFullMessageResponse(11, "gamer_not_exist");
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public static JsonObject getGamerById(long id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_gamer WHERE user_id = ?";
            JsonObject data = bridge.queryOne(query, id);
            if (data == null) {
                return BaseResponse.createFullMessageResponse(11, "gamer_not_exist");
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject update(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long user_id = data.get("user_id").getAsLong();
            JsonObject response = getGamerByNickName(data.get("nick_name").getAsString());
            if (BaseResponse.isSuccessFullMessage(response) && response.getAsJsonObject("data").get("user_id").getAsLong() != user_id) {
                return BaseResponse.createFullMessageResponse(13, "nick_name_exists");
            }
            response = getGamerById(user_id);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return response;
            }
            JsonObject oldData = response.getAsJsonObject("data");
            oldData.add("nick_name", data.get("nick_name"));
            oldData.add("full_name", data.get("full_name"));
            oldData.add("avatar", data.get("avatar"));
            oldData.add("detail", data.get("detail"));
            oldData.add("clan_id", data.get("clan_id"));
            oldData.add("rank", data.get("rank"));
            oldData.add("rank_info", data.get("rank_info"));
            oldData.add("state", data.get("state"));
            bridge.updateObjectToDb("aoe_gamer", "user_id", oldData);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stackTrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stackTrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listGamer(long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject result = new JsonObject();
            long offset = (page - 1) * recordPerPage;
            String query = "SELECT * FROM aoe_gamer LIMIT ? OFFSET ?";
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
    public JsonObject listGamerByClanId(long clanId, long page, long recordPerPage) {
        try {
            JsonObject response = AoeServices.clanService.getClanById(clanId);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return response;
            }
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject result = new JsonObject();
            long offset = (page - 1) * recordPerPage;
            String query = "SELECT * FROM aoe_gamer WHERE clan_id = ? LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, clanId, recordPerPage, offset);
            result.add("gamer", data);
            return BaseResponse.createFullMessageResponse(0, "success", result);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    /*These function user for TEST only. In real situation these actions is prohibited*/
    @Override
    public JsonObject deleteGamerByUserId(long id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "DELETE FROM aoe_gamer WHERE user_id = ?";
            int x = bridge.update(query, id);
            if (x == 1) return BaseResponse.createFullMessageResponse(0, "success");
            return BaseResponse.createFullMessageResponse(10, "not_found_gamer");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getByPhone(String phone) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_gamer WHERE phone = ?";
            JsonObject data = bridge.queryOne(query, phone);
            if (data == null) {
                return BaseResponse.createFullMessageResponse(11, "gamer_not_exist");
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject gamerUpdateStatistic(long id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject response = AoeServices.donateService.statisticDonateByTargetId(StarConstant.SERVICE_DONATE_GAMER, id);
            DebugLogger.info("{}", response);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return response;
            }
            JsonObject data = response.getAsJsonObject("data");
            long total_star_donate = data.get("total_star_donate").getAsLong();
            long total_supporter = data.get("total_supporter").getAsLong();
            String query = "UPDATE aoe_gamer SET total_star_donate = ?, total_supporter = ?  WHERE user_id = ?";
            bridge.update(query, total_star_donate, total_supporter, id);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
