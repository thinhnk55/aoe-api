package vn.vietdefi.aoe.services.star;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
                return createStarWallet(userId);
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
            String query = "SELECT * FROM aoe_star_transaction WHERE user_id = ? AND service = ? LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, userId, service, recordPerPage, offset);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listStarTransactionOfUser(long userId, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            String query = "SELECT * FROM aoe_star_transaction WHERE user_id = ? LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, userId, recordPerPage, offset);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listStarTransactionOfUserByTime(long time, long userId, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            String query = "SELECT * FROM aoe_star_transaction WHERE user_id = ? AND create_time > ? ORDER BY create_time DESC LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, userId, recordPerPage, offset);
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
            addReferToTransaction(data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    private void addReferToTransaction(JsonObject data) {
        long referId = data.get("refer_id").getAsLong();
        if (referId == 0) {
            data.add("refer", null);
            return;
        }
        int service = data.get("service").getAsInt();
        if (service == StarConstant.SERVICE_STAR_RECHARGE) {
            JsonObject response = BankServices.bankService.getBalanceTransactionById(referId);
            if (BaseResponse.isSuccessFullMessage(response)) {
                JsonObject account = response.getAsJsonObject("data");
                data.add("refer", account);
            }
        }
    }

    @Override
    public JsonObject exchangeStar(long amount, int service, String username, long referId) {
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
                            if(result.next()){
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
            String query = "SELECT balance FROM aoe_star WHERE userid = ?";
            long data = bridge.queryInteger(query, userid);
            return data >= amount;
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return false;
        }
    }
}
