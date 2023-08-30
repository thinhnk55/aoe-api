package vn.vietdefi.aoe.services.donate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.aoe.services.statistic.logic.StatisticController;
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
            String avatar = profile.get("avatar").getAsString();
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
            data.addProperty("avatar", avatar);
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
            if (service == StarConstant.SERVICE_DONATE_MATCH) {
                AoeServices.matchService.addStarCurrentMatch(target_id, star);
                StatisticController.instance().starDonateToEntity(StarConstant.SERVICE_DONATE_MATCH, star);
            }
            if (service == StarConstant.SERVICE_DONATE_LEAGUE) {
                AoeServices.leagueService.addStarForLeague(target_id, star);
                StatisticController.instance().starDonateToEntity(StarConstant.SERVICE_DONATE_LEAGUE, star);
            }
            if (service == StarConstant.SERVICE_DONATE_GAMER) {
                AoeServices.gamerService.gamerUpdateStatistic(target_id);
                StatisticController.instance().starDonateToEntity(StarConstant.SERVICE_DONATE_GAMER, star);
            }
            if (service == StarConstant.SERVICE_DONATE_CASTER) {
                AoeServices.casterService.casterUpdateStatistic(target_id);
                StatisticController.instance().starDonateToEntity(StarConstant.SERVICE_DONATE_CASTER, star);
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
            if (data == null) {
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
            JsonObject target = getTargetById(service, targetId);
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            String query = "SELECT * FROM aoe_donate WHERE target_id = ? AND service = ? ORDER BY id DESC LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, targetId, service, recordPerPage, offset);
            JsonObject result = new JsonObject();
            result.add("donate", data);
            result.add("target", target);
            return BaseResponse.createFullMessageResponse(0, "success", result);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    private JsonObject getTargetById(int service, long targetId) {
        switch (service) {
            case StarConstant.SERVICE_DONATE_GAMER:
                JsonObject gamerResponse = AoeServices.gamerService.getPartialGamer(targetId);
                if (BaseResponse.isSuccessFullMessage(gamerResponse)) {
                    return gamerResponse.getAsJsonObject("data");
                }
                break;
            case StarConstant.SERVICE_DONATE_CASTER:
                JsonObject casterResponse = AoeServices.casterService.getPartialCaster(targetId);
                if (BaseResponse.isSuccessFullMessage(casterResponse)) {
                    return casterResponse.getAsJsonObject("data");
                }
                break;
            case StarConstant.SERVICE_DONATE_MATCH:
                JsonObject matchResponse = AoeServices.matchService.getById(targetId);
                if (BaseResponse.isSuccessFullMessage(matchResponse)) {
                    return matchResponse.getAsJsonObject("data");
                }
                break;
            case StarConstant.SERVICE_SUGGEST_MATCH:
                JsonObject matchSuggestResponse = AoeServices.suggestService.getMatchSuggest(targetId);
                if (BaseResponse.isSuccessFullMessage(matchSuggestResponse)) {
                    return matchSuggestResponse.getAsJsonObject("data");
                }
                break;
            case StarConstant.SERVICE_DONATE_LEAGUE:
                JsonObject leagueResponse = AoeServices.leagueService.getLeagueInfo(targetId);
                if (BaseResponse.isSuccessFullMessage(leagueResponse)) {
                    return leagueResponse.getAsJsonObject("data");
                }
                break;
        }
        return null;
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
        if (service == StarConstant.SERVICE_DONATE_LEAGUE) {
            return AoeServices.leagueService.getLeagueInfo(targetId);
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
    public JsonObject statisticTotalDonate(long from, long to) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT COUNT(DISTINCT user_id) AS total_user_donate, COALESCE(SUM(amount), 0) AS total_star_donate FROM aoe_donate WHERE create_time > ? AND create_time < ?";
            JsonObject response = bridge.queryOne(query, from, to);
            query = new StringBuilder("SELECT service, COALESCE(SUM(amount), 0) as total_star_donate\n")
                    .append("FROM aoe_donate \n ")
                    .append("WHERE service IN (?,?,?,?)\n")
                    .append(" AND create_time > ? AND create_time < ?\n")
                    .append("GROUP BY service")
                    .toString();
            JsonArray array = bridge.query(query,
                    StarConstant.SERVICE_DONATE_MATCH,
                    StarConstant.SERVICE_DONATE_GAMER,
                    StarConstant.SERVICE_DONATE_CASTER,
                    StarConstant.SERVICE_DONATE_LEAGUE,
                    from,
                    to);
            response.addProperty("total_star_donate_for_match", 0);
            response.addProperty("total_star_donate_for_gamer", 0);
            response.addProperty("total_star_donate_for_caster", 0);
            response.addProperty("total_star_donate_for_league", 0);

            for (int i = 0; i < array.size(); i++) {
                switch (array.get(i).getAsJsonObject().get("service").getAsInt()) {
                    case StarConstant.SERVICE_DONATE_MATCH:
                        response.addProperty("total_star_donate_for_match", array.get(i).getAsJsonObject().get("total_star_donate").getAsLong());
                        break;
                    case StarConstant.SERVICE_DONATE_GAMER:
                        response.addProperty("total_star_donate_for_gamer", array.get(i).getAsJsonObject().get("total_star_donate").getAsLong());
                        break;
                    case StarConstant.SERVICE_DONATE_CASTER:
                        response.addProperty("total_star_donate_for_caster", array.get(i).getAsJsonObject().get("total_star_donate").getAsLong());
                        break;
                    case StarConstant.SERVICE_DONATE_LEAGUE:
                        response.addProperty("total_star_donate_for_league", array.get(i).getAsJsonObject().get("total_star_donate").getAsLong());
                        break;
                }
            }
            return BaseResponse.createFullMessageResponse(0, "success", response);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listDonateOfUser(long userId, long page, long recordPerPage) {
        try {
            long offset = (page - 1) * recordPerPage;
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_donate WHERE user_id = ? AND service IN (?,?) LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, userId, StarConstant.SERVICE_DONATE_GAMER, StarConstant.SERVICE_DONATE_CASTER, recordPerPage, offset);
            for (JsonElement element : data) {
                int service = element.getAsJsonObject().get("service").getAsInt();
                long targetId = element.getAsJsonObject().get("target_id").getAsLong();
                JsonObject target = getTargetById(service, targetId);
                element.getAsJsonObject().add("target", target);
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listGamerFavorites(long userId, long page, long recordPerPage) {
        try {
            long offset = (page - 1) * recordPerPage;
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT target_id FROM aoe_donate WHERE user_id = ? AND service = ? LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, userId, StarConstant.SERVICE_DONATE_GAMER, recordPerPage, offset);
            JsonArray gamers = new JsonArray();
            for (JsonElement element : data) {
                long targetId = element.getAsJsonObject().get("target_id").getAsLong();
                JsonObject target = getTargetById(StarConstant.SERVICE_DONATE_GAMER, targetId);
                gamers.add(target);
                element.getAsJsonObject().add("target", target);
            }
            return BaseResponse.createFullMessageResponse(0, "success", gamers);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject filterListDonate(String phoneNumber, long from, long to, int service, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            StringBuilder query = new StringBuilder("SELECT * FROM aoe_donate WHERE ");

            if (!phoneNumber.isEmpty()) {
                query.append("phone = ? AND ");
            }
            if (service != 0) {
                query.append("service = ? AND ");
            }
            query.append("create_time > ? AND create_time < ? ");
            query.append("ORDER BY create_time DESC LIMIT ? OFFSET ?");
            JsonArray response;
            if (phoneNumber.isEmpty() && service == 0) {
                response = bridge.query(query.toString(), from, to, recordPerPage, offset);
            } else if (phoneNumber.isEmpty()) {
                response = bridge.query(query.toString(), service, from, to, recordPerPage, offset);
            } else if (service == 0) {
                response = bridge.query(query.toString(), phoneNumber, from, to, recordPerPage, offset);
            } else {
                response = bridge.query(query.toString(), phoneNumber, service, from, to, recordPerPage, offset);
            }
            JsonObject receiver = new JsonObject();
            for (JsonElement trans : response) {
                service = trans.getAsJsonObject().get("service").getAsInt();
                switch (service) {
                    case StarConstant.SERVICE_DONATE_GAMER:
                        receiver = AoeServices.gamerService.getGamerByUserId(trans.getAsJsonObject().get("target_id").getAsLong());
                        trans.getAsJsonObject().addProperty("receiver_nick_name", receiver.get("data").getAsJsonObject().get("nick_name").getAsString());
                        trans.getAsJsonObject().addProperty("receiver_avatar", receiver.get("data").getAsJsonObject().get("avatar").getAsString());
                        break;
                    case StarConstant.SERVICE_DONATE_CASTER:
                        receiver = AoeServices.casterService.getCasterByUserId(trans.getAsJsonObject().get("target_id").getAsLong());
                        trans.getAsJsonObject().addProperty("receiver_nick_name", receiver.get("data").getAsJsonObject().get("nick_name").getAsString());
                        trans.getAsJsonObject().addProperty("receiver_avatar", receiver.get("data").getAsJsonObject().get("avatar").getAsString());
                        break;
                }
            }
            return BaseResponse.createFullMessageResponse(0, "success", response);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject filterStatisticDonate(long userId, int service, long targetId, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            StringBuilder query = new StringBuilder("SELECT * FROM aoe_donate WHERE user_id = ? ");
            StringBuilder queryTotal = new StringBuilder("SELECT COALESCE(SUM(amount), 0) AS total_star_donate FROM aoe_donate WHERE user_id = ? ");
            if (targetId != 0 && service != 0) {
                query.append("AND service = ? AND target_id = ? ");
                queryTotal.append("AND service = ? AND target_id = ?");
            } else if(targetId == 0 && service != 0) {
                query.append("AND service = ? ");
                queryTotal.append("AND service = ?");
            } else if(targetId != 0 && service == 0){
                query.append("AND target_id = ? ");
                queryTotal.append("AND target_id = ?");
            }
            query.append("ORDER BY id DESC LIMIT ? OFFSET ?");
            JsonArray donate;
            long totalStarDonate;
            if (targetId != 0 && service != 0) {
                donate = bridge.query(query.toString(), userId, service, targetId, recordPerPage, (page - 1) * recordPerPage);
                totalStarDonate = bridge.queryLong(queryTotal.toString(), userId, service, targetId);
            } else if (targetId == 0 && service != 0) {
                donate = bridge.query(query.toString(), userId, service, recordPerPage, (page - 1) * recordPerPage);
                totalStarDonate = bridge.queryLong(queryTotal.toString(), userId, service);
            }
            else if (targetId != 0 && service == 0){
                donate = bridge.query(query.toString(), userId, targetId, recordPerPage, (page - 1) * recordPerPage);
                totalStarDonate = bridge.queryLong(queryTotal.toString(), userId, targetId);
            } else {
                donate = bridge.query(query.toString(), userId, recordPerPage, (page - 1) * recordPerPage);
                totalStarDonate = bridge.queryLong(queryTotal.toString(), userId);
            }
            JsonObject data = new JsonObject();
            data.addProperty("total_star_donate", totalStarDonate);
            data.add("donate", donate);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject refundStarDonate(long matchId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_donate WHERE service = ? AND target_id = ? ";
            JsonArray data = bridge.query(query, StarConstant.SERVICE_DONATE_MATCH, matchId);
            for (JsonElement dataElement : data) {
                long id = dataElement.getAsJsonObject().get("id").getAsLong();
                long userId = dataElement.getAsJsonObject().get("user_id").getAsLong();
                long amount = dataElement.getAsJsonObject().get("amount").getAsLong();
                JsonObject response = AoeServices.starService.exchangeStar(userId, StarConstant.SERVICE_DONATE_MATCH_REFUND, amount, id);
                if (BaseResponse.isSuccessFullMessage(response)) {
                    response = updateStateDonate(id, DonateState.REFUND);
                    if (BaseResponse.isSuccessFullMessage(response)) {
                        return response;
                    }
                }
            }
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }




    private JsonObject updateStateDonate(long id, int state) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE aoe_donate SET state = ? WHERE id = ? ";
            int row = bridge.update(query, state, id);
            if (row == 0) {
                return BaseResponse.createFullMessageResponse(10, "not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject statisticDonateByTargetId(int service, long targetId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT COUNT(DISTINCT user_id) AS total_supporter, COALESCE(SUM(amount), 0) AS total_star_donate FROM aoe_donate WHERE target_id = ? AND service = ?";
            JsonObject data = bridge.queryOne(query, targetId, service);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject listDonateOutstanding() {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            StringBuilder query = new StringBuilder("SELECT * FROM aoe_donate WHERE ");
            query.append("service IN(?,?) ");
            query.append("ORDER BY create_time DESC LIMIT 10");
            JsonArray response;

            response = bridge.query(query.toString(),StarConstant.SERVICE_DONATE_GAMER,StarConstant.SERVICE_DONATE_CASTER);
            JsonObject receiver = new JsonObject();
            for (JsonElement trans : response) {
                int service = trans.getAsJsonObject().get("service").getAsInt();
                switch (service) {
                    case StarConstant.SERVICE_DONATE_GAMER:
                        receiver = AoeServices.gamerService.getGamerByUserId(trans.getAsJsonObject().get("target_id").getAsLong());
                        trans.getAsJsonObject().addProperty("receiver_nick_name", receiver.get("data").getAsJsonObject().get("nick_name").getAsString());
                        trans.getAsJsonObject().addProperty("receiver_avatar", receiver.get("data").getAsJsonObject().get("avatar").getAsString());
                        break;
                    case StarConstant.SERVICE_DONATE_CASTER:
                        receiver = AoeServices.casterService.getCasterByUserId(trans.getAsJsonObject().get("target_id").getAsLong());
                        trans.getAsJsonObject().addProperty("receiver_nick_name", receiver.get("data").getAsJsonObject().get("nick_name").getAsString());
                        trans.getAsJsonObject().addProperty("receiver_avatar", receiver.get("data").getAsJsonObject().get("avatar").getAsString());
                        break;
                }
            }
            return BaseResponse.createFullMessageResponse(0, "success", response);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }


    public JsonObject statisticDonateByUserId(long userid) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = new StringBuilder("SELECT service, COALESCE(SUM(amount), 0) as total_star_donate\n")
                    .append("FROM aoe_donate \n ")
                    .append("WHERE service IN (?,?,?,?) AND user_id = ?\n")
                    .append("GROUP BY service")
                    .toString();
            JsonArray array = bridge.query(query,
                    StarConstant.SERVICE_DONATE_MATCH,
                    StarConstant.SERVICE_DONATE_GAMER,
                    StarConstant.SERVICE_DONATE_CASTER,
                    StarConstant.SERVICE_DONATE_LEAGUE,
                    userid
                    );
            JsonObject response =new JsonObject();
            response.addProperty("total_star_donate_for_match", 0);
            response.addProperty("total_star_donate_for_gamer", 0);
            response.addProperty("total_star_donate_for_caster", 0);
            response.addProperty("total_star_donate_for_league", 0);
            long total_star_donate = 0;
            for (int i = 0; i < array.size(); i++) {
                switch (array.get(i).getAsJsonObject().get("service").getAsInt()) {
                    case StarConstant.SERVICE_DONATE_MATCH:
                        response.addProperty("total_star_donate_for_match", array.get(i).getAsJsonObject().get("total_star_donate").getAsLong());
                        total_star_donate += array.get(i).getAsJsonObject().get("total_star_donate").getAsLong();
                        break;
                    case StarConstant.SERVICE_DONATE_GAMER:
                        response.addProperty("total_star_donate_for_gamer", array.get(i).getAsJsonObject().get("total_star_donate").getAsLong());
                        total_star_donate += array.get(i).getAsJsonObject().get("total_star_donate").getAsLong();
                        break;
                    case StarConstant.SERVICE_DONATE_CASTER:
                        response.addProperty("total_star_donate_for_caster", array.get(i).getAsJsonObject().get("total_star_donate").getAsLong());
                        total_star_donate += array.get(i).getAsJsonObject().get("total_star_donate").getAsLong();
                        break;
                    case StarConstant.SERVICE_DONATE_LEAGUE:
                        response.addProperty("total_star_donate_for_league", array.get(i).getAsJsonObject().get("total_star_donate").getAsLong());
                        total_star_donate += array.get(i).getAsJsonObject().get("total_star_donate").getAsLong();
                        break;
                }
            }
            response.addProperty("total_star_donate",total_star_donate);
            return BaseResponse.createFullMessageResponse(0, "success", response);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
