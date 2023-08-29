package vn.vietdefi.aoe.services.star;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.bank.BankServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class StarService implements IStarService {
    private JsonObject createStarWallet(long userId) {
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
                data.addProperty("balance", 0);
                String query = "INSERT INTO aoe_star(user_id, username) VALUES(?,?)";
                bridge.update(query, userId, username);
                return BaseResponse.createFullMessageResponse(0, "success", data);
            }
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getStarWalletByUserId(long userId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_star WHERE user_id = ?";
            JsonObject data = bridge.queryOne(query, userId);
            if (data == null) {
                data = createStarWallet(userId);
            }
            JsonObject profile = AoeServices.profileService.getUserProfileByUserId(userId);
            if (BaseResponse.isSuccessFullMessage(profile)) {
                data.addProperty("nick_name", profile.getAsJsonObject("data")
                        .get("nick_name").getAsString());
                data.addProperty("avatar", profile.getAsJsonObject("data")
                        .get("avatar").getAsString());
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listStarTransactionOfUserByService(long userId, int service, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            String query = "SELECT * FROM aoe_star_transaction WHERE user_id = ? AND service = ? ORDER BY id DESC LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, userId, service, recordPerPage, offset);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listStarTransactionOfUserAll(long userId, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            String query = "SELECT * FROM aoe_star_transaction WHERE user_id = ? ORDER BY id DESC LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, userId, recordPerPage, offset);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listStarTransactionOfUserByTime(long userId, long from, long to, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            String query = new StringBuilder("SELECT * FROM aoe_star_transaction WHERE user_id = ? ")
                    .append("AND create_time >= ? AND create_time < ? ORDER BY id DESC LIMIT ? OFFSET ?")
                    .toString();
            JsonArray data = bridge.query(query, userId, from, to, recordPerPage, offset);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getStarTransactionById(long id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_star_transaction WHERE id = ?";
            JsonObject data = bridge.queryOne(query, id);
            if (data == null)
                return BaseResponse.createFullMessageResponse(10, "star_transaction_not_exit");
            addReferToTransaction(data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    private void addReferToTransaction(JsonObject data) {
        long referId = data.get("refer_id").getAsLong();
        long userId = data.get("user_id").getAsLong();
        JsonObject response = AoeServices.profileService.getUserProfileByUserId(userId);
        data.add("profile", response.getAsJsonObject("data"));
        if (referId == 0) {
            data.add("refer", null);
            return;
        }
        int service = data.get("service").getAsInt();
        if (service == StarConstant.SERVICE_STAR_RECHARGE) {
            JsonObject responseBank = BankServices.bankTransactionService.getBalanceTransactionById(referId);
            if (BaseResponse.isSuccessFullMessage(responseBank)) {
                JsonObject account = responseBank.getAsJsonObject("data");
                data.add("refer", account);
            }
        }
//        response = AoeServices.donateService.getDonateById(referId);
//        data.addProperty("message", response.getAsJsonObject("data").get("message").getAsString());
//        } else if (service == StarConstant.SERVICE_DONATE_GAMER) {
//            if (BaseResponse.isSuccessFullMessage(response)) {
//                long id = response.getAsJsonObject("data").get("target_id").getAsLong();
//                JsonObject gamer = AoeServices.gamerService.getGamerByUserId(id);
//                data.add("gamer", gamer.getAsJsonObject("data"));
//            }
//        } else if (service == StarConstant.SERVICE_DONATE_CASTER){
//            if (BaseResponse.isSuccessFullMessage(response)) {
//                long id = response.getAsJsonObject("data").get("target_id").getAsLong();
//                JsonObject caster = AoeServices.casterService.getCasterByUserId(id);
//                data.add("caster", caster.getAsJsonObject("data"));
//            }
//        } else if (service == StarConstant.SERVICE_DONATE_MATCH) {
//            if (BaseResponse.isSuccessFullMessage(response)) {
//                long id = response.getAsJsonObject("data").get("target_id").getAsLong();
//                JsonObject match = AoeServices.matchService.getById(id);
//                data.add("match", match.getAsJsonObject("data"));
//            }
//        } else if (service == StarConstant.SERVICE_SUGGEST_MATCH) {
//            if (BaseResponse.isSuccessFullMessage(response)) {
//                long id = response.getAsJsonObject("data").get("target_id").getAsLong();
//                JsonObject suggestMatch = AoeServices.suggestService.getMatchSuggest(id);
//                data.add("suggest_match", suggestMatch.getAsJsonObject("data"));
//            }
//        } else if (service == StarConstant.SERVICE_DONATE_LEAGUE) {
//            //TODO:
//        }
    }

    @Override
    public JsonObject exchangeStar(String username, int service, long amount, long referId) {
        String query1 = "SELECT * FROM aoe_star WHERE username = ? FOR UPDATE";
        String query2 = "UPDATE aoe_star SET balance = ? WHERE username = ?";
        String query3 = "INSERT INTO aoe_star_transaction (user_id, username, service, refer_id, amount, balance, create_time) VALUE (?,?,?,?,?,?,?)";
        String query4 = "SELECT * FROM aoe_star_transaction WHERE id = ?";
        try (Connection connection = HikariClients.instance().defaulSQLJavaBridge().sqlClient.getConnection();
             PreparedStatement st1 = connection.prepareStatement(query1);
             PreparedStatement st2 = connection.prepareStatement(query2);
             PreparedStatement st3 = connection.prepareStatement(query3, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement st4 = connection.prepareStatement(query4)) {
            connection.setAutoCommit(false);
            st1.setString(1, username);
            try (ResultSet rs = st1.executeQuery()) {
                if (rs.next()) {
                    long userId = rs.getLong("user_id");
                    int balance = rs.getInt("balance");
                    long newBalance = balance + amount;
                    if (newBalance < 0) {
                        return BaseResponse.createFullMessageResponse(12, "insufficient_balance");
                    }
                    //update star wallet
                    st2.setLong(1, newBalance);
                    st2.setString(2, username);
                    int rowUpdateStarWallet = st2.executeUpdate();
                    if (rowUpdateStarWallet == 0) {
                        return BaseResponse.createFullMessageResponse(11, "failure");
                    }
                    //insert star transaction
                    st3.setLong(1, userId);
                    st3.setString(2, username);
                    st3.setInt(3, service);
                    st3.setLong(4, referId);
                    st3.setLong(5, amount);
                    st3.setLong(6, newBalance);
                    st3.setLong(7, System.currentTimeMillis());
                    int rowInsertStarTransaction = st3.executeUpdate();
                    if (rowInsertStarTransaction == 0) {
                        connection.rollback();
                        return BaseResponse.createFullMessageResponse(11, "failure");
                    } else {
                        //get id aoe transaction
                        JsonObject data;
                        try (ResultSet result = st3.getGeneratedKeys()) {
                            if (result.next()) {
                                long id = result.getLong(1);
                                st4.setLong(1, id);
                            }
                            try (ResultSet rs4 = st4.executeQuery()) {
                                data = GsonUtil.toJsonObject(rs4);
                            }
                        }
                        connection.commit();
                        return BaseResponse.createFullMessageResponse(0, "success", data);
                    }
                } else {
                    return BaseResponse.createFullMessageResponse(10, "not_found");
                }
            }
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject exchangeStar(long userId, int service, long amount, long referId) {
        String query1 = "SELECT * FROM aoe_star WHERE user_id = ? FOR UPDATE";
        String query2 = "UPDATE aoe_star SET balance = ? WHERE username = ?";
        String query3 = "INSERT INTO aoe_star_transaction (user_id, username, service, refer_id, amount, balance, create_time) VALUE (?,?,?,?,?,?,?)";
        String query4 = "SELECT * FROM aoe_star_transaction WHERE id = ?";
        try (Connection connection = HikariClients.instance().defaulSQLJavaBridge().sqlClient.getConnection();
             PreparedStatement st1 = connection.prepareStatement(query1);
             PreparedStatement st2 = connection.prepareStatement(query2);
             PreparedStatement st3 = connection.prepareStatement(query3, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement st4 = connection.prepareStatement(query4)) {
            connection.setAutoCommit(false);
            st1.setLong(1, userId);
            try (ResultSet rs = st1.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    int balance = rs.getInt("balance");
                    long newBalance = balance + amount;
                    if (newBalance < 0) {
                        return BaseResponse.createFullMessageResponse(12, "insufficient_balance");
                    }
                    //update star wallet
                    st2.setLong(1, newBalance);
                    st2.setString(2, username);
                    int rowUpdateStarWallet = st2.executeUpdate();
                    if (rowUpdateStarWallet == 0) {
                        return BaseResponse.createFullMessageResponse(11, "failure");
                    }
                    //insert star transaction
                    st3.setLong(1, userId);
                    st3.setString(2, username);
                    st3.setInt(3, service);
                    st3.setLong(4, referId);
                    st3.setLong(5, amount);
                    st3.setLong(6, newBalance);
                    st3.setLong(7, System.currentTimeMillis());
                    int rowInsertStarTransaction = st3.executeUpdate();
                    if (rowInsertStarTransaction == 0) {
                        connection.rollback();
                        return BaseResponse.createFullMessageResponse(11, "failure");
                    } else {
                        //get id aoe transaction
                        JsonObject data;
                        try (ResultSet result = st3.getGeneratedKeys()) {
                            if (result.next()) {
                                long id = result.getLong(1);
                                st4.setLong(1, id);
                            }
                            try (ResultSet rs4 = st4.executeQuery()) {
                                data = GsonUtil.toJsonObject(rs4);
                            }
                        }
                        connection.commit();
                        return BaseResponse.createFullMessageResponse(0, "success", data);
                    }
                } else {
                    return BaseResponse.createFullMessageResponse(10, "not_found");
                }
            }
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject lookupRechargeHistory(String phoneNumber, long from, long to, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            StringBuilder query = new StringBuilder("SELECT * FROM aoe_star_transaction WHERE ");
            long offset = (page - 1) * recordPerPage;
            boolean isPhoneExist = false;
            if (!phoneNumber.isEmpty()) {
                    query.append("username = ? AND ");
                    isPhoneExist = true;
            }
            query.append("create_time >= ? AND create_time < ? ORDER BY id DESC LIMIT ? OFFSET ?");
            DebugLogger.info("{}", query);
            JsonArray transactions;
            if (isPhoneExist) {
                transactions = bridge.query(query.toString(), phoneNumber, from, to, recordPerPage, offset);
            } else {
                transactions = bridge.query(query.toString(), from, to, recordPerPage, offset);
            }
            JsonObject data = new JsonObject();
            data.add("transaction", transactions);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public void updateReferId(long id, long referId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE aoe_star_transaction SET refer_id = ? WHERE id = ?";
            bridge.update(query, referId, id);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    @Override
    public boolean checkStar(long amount, long userid) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT balance FROM aoe_star WHERE user_id = ?";
            long data = bridge.queryInteger(query, userid);
            return data >= amount;
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }

    public JsonObject getListRefundDonate(String phoneNumber, long from, long to, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            StringBuilder query = new StringBuilder("SELECT * FROM aoe_star_transaction WHERE ");

            if (!phoneNumber.isEmpty()) {
                query.append("username = ? AND ");
            }
            query.append("create_time > ? AND create_time < ? AND service = ? ");
            query.append("ORDER BY create_time DESC LIMIT ? OFFSET ?");
            JsonArray response;

            if (phoneNumber.isEmpty()) {
                response = bridge.query(query.toString(), from, to,StarConstant.SERVICE_DONATE_MATCH_REFUND, recordPerPage, offset);
            } else {
                response = bridge.query(query.toString(), phoneNumber, from, to,StarConstant.SERVICE_DONATE_MATCH_REFUND, recordPerPage, offset);
            }
            for (JsonElement trans :response) {
                trans.getAsJsonObject().add("refer_transaction",
                        AoeServices.donateService.getDonateById(trans.getAsJsonObject().get("refer_id").getAsLong()));
            }
            return BaseResponse.createFullMessageResponse(0, "success", response);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getStatisticRecharge(long from, long to) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query =  "SELECT COUNT(DISTINCT user_id) AS total_user_recharge, COALESCE(SUM(amount), 0) AS total_star_recharge FROM aoe_star_transaction WHERE  create_time > ? AND create_time < ? AND service = ?  ";
            JsonObject response = bridge.queryOne(query,from,to,StarConstant.SERVICE_STAR_RECHARGE);
            return BaseResponse.createFullMessageResponse(0, "success", response);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    /*These function user for TEST only. In real situation these actions is prohibited*/
    @Override
    public JsonObject deleteStarWallet(long userId) {
        String query1 = "DELETE FROM aoe_star WHERE user_id = ?";
        String query2 = "DELETE FROM aoe_star_transaction WHERE user_id = ?";
        try (Connection connection = HikariClients.instance().defaulSQLJavaBridge().sqlClient.getConnection();
             PreparedStatement st1 = connection.prepareStatement(query1);
             PreparedStatement st2 = connection.prepareStatement(query2)) {
            connection.setAutoCommit(false);
            st1.setLong(1, userId);
            st2.setLong(1, userId);
            try {
                st1.executeUpdate();
                st2.executeUpdate();
            } catch (Exception e) {
                connection.rollback();
            }
            connection.commit();
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
