package vn.vietdefi.aoe.services.donate;

import com.google.gson.JsonArray;
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
            response = AoeServices.starService.exchangeStar(username, StarConstant.SERVICE_DONATE_MATCH, -star,
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
            AoeServices.matchService.addStarCurrentMatch(target_id, star);
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
    public JsonObject listDonateByTargetId(long targetId, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            String query = "SELECT * FROM aoe_donate WHERE target_id = ? ORDER BY create_time DESC LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, targetId, recordPerPage, offset);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listAllTopDonate(long time, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            long currentTime = System.currentTimeMillis();
            long totalMilliseconds = time * DonateConstants.MILLISECONDS_PER_DAY;
            currentTime = currentTime - totalMilliseconds;
            String query = "SELECT user_id, username, SUM(amount) as total_star, phone, nick_name FROM aoe_donate WHERE create_time > ? GROUP BY user_id ORDER BY total_star DESC LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, currentTime, recordPerPage, offset);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listTopDonateByTargetId(long time, long targetId, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            long currentTime = System.currentTimeMillis();
            long totalMilliseconds = time * DonateConstants.MILLISECONDS_PER_DAY;
            currentTime = currentTime - totalMilliseconds;
            String query = "SELECT user_id, username, SUM(amount) as total_star, phone, nick_name FROM aoe_donate WHERE target_id = ? AND create_time > ? GROUP BY user_id ORDER BY total_star DESC LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, targetId,currentTime, recordPerPage, offset);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public long getTotalDonateByUserId(long id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT count(*) FROM aoe_donate_gamer WHERE gamer_id = ? GROUP BY gamer_id";
            return bridge.queryLong(query, id);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return 0;
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

}
