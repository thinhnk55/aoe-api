package vn.vietdefi.aoe.services.donate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class DonateService implements IDonateService {
    @Override
    public JsonObject donate(long sender, long star, int service, long target_id, String message) {
        try {
            JsonObject response = AoeServices.profileService.getUserProfileByUserId(sender);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return BaseResponse.createFullMessageResponse(10, "profile_not_found");
            }
            JsonObject profile = response.getAsJsonObject("data");
            String username = profile.get("username").getAsString();
            String nick_name = profile.get("nick_name").getAsString();
            response = verifyTarget(service, target_id);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return BaseResponse.createFullMessageResponse(11, "donate_reject");
            }
            //Tru sao trong tai khoan message.sender
            response = AoeServices.starService.exchangeStar(username, service, -star,
                    0);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return BaseResponse.createFullMessageResponse(12, "exchange_star_failed");
            }
            //Tao giao dich donate
            JsonObject starTransaction = response.getAsJsonObject("data");
            long sub_star_transaction_id = starTransaction.get("id").getAsLong();
            JsonObject data = new JsonObject();
            data.addProperty("user_id", sender);
            data.addProperty("username", username);
            data.addProperty("nick_name", nick_name);
            data.addProperty("phone", username);
            data.addProperty("amount", star);
            data.addProperty("service", service);
            data.addProperty("target_id", target_id);
            data.addProperty("message", message);
            data.addProperty("sub_star_transaction_id", sub_star_transaction_id);
            data.addProperty("add_star_transaction_id", 0);
            data.addProperty("state", DonateState.WAITING);
            data.addProperty("create_time", starTransaction.get("create_time").getAsLong());
            response = createDonate(data);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return BaseResponse.createFullMessageResponse(33, "create_donate_failed");
            }
            JsonObject donate = response.getAsJsonObject("data");
            long donate_id = donate.get("id").getAsLong();
            AoeServices.starService.updateReferId(sub_star_transaction_id,
                    donate_id);
            if (service == StarConstant.SERVICE_DONATE_GAMER
                    || service == StarConstant.SERVICE_DONATE_CASTER) {
                response = AoeServices.starService.exchangeStar(target_id, service, star,
                        donate_id);
                if (BaseResponse.isSuccessFullMessage(response)) {
                    long add_star_transaction_id = response.getAsJsonObject("data")
                            .get("id").getAsLong();
                    updateDonateUsed(donate_id, add_star_transaction_id);
                    donate.addProperty("add_star_transaction_id", add_star_transaction_id);
                    donate.addProperty("state", DonateState.USED);
                }
            }
            if(service == StarConstant.SERVICE_DONATE_MATCH) {
                AoeServices.matchService.addStarCurrentMatch(target_id, star);
            }
            if(service == StarConstant.SERVICE_DONATE_GAMER) {
                AoeServices.gamerService.gamerUpdateStatistic(target_id);
            }
            if(service == StarConstant.SERVICE_DONATE_CASTER) {
                AoeServices.casterService.casterUpdateStatistic(target_id);

            }
            return BaseResponse.createFullMessageResponse(0, "success", donate);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject createDonate(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            bridge.insertObjectToDB("aoe_donate", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    @Override
    public JsonObject getDonateById(long id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_donate WHERE id = ?";
            JsonObject data = bridge.queryOne(query, id);
            if(data == null){
                return BaseResponse.createFullMessageResponse(10, "donate_not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listDonateByTargetId(int service, long targetId, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            String query = "SELECT * FROM aoe_donate WHERE target_id = ? AND service = ? ORDER BY id DESC LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, targetId, service, recordPerPage, offset);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listAllTopDonate(long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            String query = "SELECT user_id, username, SUM(amount) as total_star, phone, nick_name FROM aoe_donate GROUP BY user_id ORDER BY total_star DESC LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, recordPerPage, offset);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listTopDonateByTargetId(long targetId, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            String query = "SELECT user_id, username, SUM(amount) as total_star, phone, nick_name FROM aoe_donate WHERE target_id = ? GROUP BY user_id ORDER BY total_star DESC LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, targetId, recordPerPage, offset);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    
    private JsonObject verifyTarget(int service, long targetId) {
        if (service == StarConstant.SERVICE_DONATE_MATCH) {
            return AoeServices.matchService.checkAcceptDonate(targetId);
        }
        if (service == StarConstant.SERVICE_DONATE_GAMER) {
            return AoeServices.gamerService.getGamerByUserId(targetId);
        }
        if (service == StarConstant.SERVICE_DONATE_CASTER) {
            return AoeServices.casterService.getCasterByUserId(targetId);
        }
        return BaseResponse.createFullMessageResponse(10, "invalid_service");
    }

    @Override
    public void updateDonateUsed(long id, long add_star_transaction_id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE aoe_donate SET add_star_transaction_id = ?, state = ? WHERE id = ?";
            bridge.update(query, add_star_transaction_id, DonateState.USED, id);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    /*These function user for TEST only. In real situation these actions is prohibited*/
    @Override
    public JsonObject deleteDonateBySenderId(long userId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "DELETE FROM aoe_donate WHERE user_id = ?";
            bridge.update(query, userId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject statisticTotalDonate() {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT COUNT(DISTINCT user_id) AS total_user_donate, COALESCE(SUM(amount), 0) AS total_star_donate FROM aoe_donate";
            JsonObject response = bridge.queryOne(query);
            query = new StringBuilder("SELECT service, COALESCE(SUM(amount), 0) as total_star_donate\n" )
                    .append("FROM aoe_donate \n ")
                    .append("WHERE service IN (?,?,?,?)\n")
                    .append("GROUP BY service")
                    .toString();
            JsonArray array = bridge.query(query,
                    StarConstant.SERVICE_DONATE_MATCH,
                    StarConstant.SERVICE_DONATE_GAMER,
                    StarConstant.SERVICE_DONATE_CASTER,
                    StarConstant.SERVICE_DONATE_LEAGUE);

            for (JsonElement total:array) {
                switch (total.getAsJsonObject().get("service").getAsInt()){
                    case StarConstant.SERVICE_DONATE_MATCH:
                        response.addProperty("total_star_donate_for_match",array.get(0).getAsJsonObject().get("total_star_donate").getAsLong());
                    case StarConstant.SERVICE_DONATE_GAMER:
                        response.addProperty("total_star_donate_for_gamer",array.get(1).getAsJsonObject().get("total_star_donate").getAsLong());
                    case StarConstant.SERVICE_DONATE_CASTER:
                        response.addProperty("total_star_donate_for_caster",array.get(2).getAsJsonObject().get("total_star_donate").getAsLong());
                    case StarConstant.SERVICE_DONATE_LEAGUE:
                        response.addProperty("total_star_donate_for_league",array.get(3).getAsJsonObject().get("total_star_donate").getAsLong());
                }
            }

            return BaseResponse.createFullMessageResponse(0, "success",response);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject statisticDonateById(int service, long targetId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query =  "SELECT COUNT(DISTINCT user_id) AS total_supporter, COALESCE(SUM(amount), 0) AS total_star_donate FROM aoe_donate WHERE target_id = ? AND service = ?";
            JsonObject data = bridge.queryOne(query, targetId, service);
            return BaseResponse.createFullMessageResponse(0, "success",data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
