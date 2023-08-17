package vn.vietdefi.aoe.services.star;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class StarService implements IStarService {
    @Override
    public JsonObject createStarWallet(long userId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject response = ApiServices.authService.get(userId);
            if (BaseResponse.isSuccessFullMessage(response)) {
                String username = response.getAsJsonObject("data").get("username").getAsString();
                String query = "INSERT INTO aoe_star(user_id, username) VALUES(?,?)";
                bridge.update(query, userId, username);
            } else {
                return response;
            }
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getStarWallet(long userId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_star WHERE user_id = ?";
            JsonObject data = bridge.queryOne(query, userId);
            if(data == null){
                return BaseResponse.createFullMessageResponse(10, "star_wallet_not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject starRechargeLog(long userId, int page) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            int offset = (page - 1) * StarConstant.SIZE_DEFAULT;
            String query = "SELECT amount, create_time FROM aoe_star WHERE user_id = ? AND service = ? LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, userId, StarConstant.STAR_RECHARGE, StarConstant.SIZE_DEFAULT, offset);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject starTransactionLog(long userId, int page) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            int offset = (page - 1) * StarConstant.SIZE_DEFAULT;
            String query = "SELECT id, amount, balance, service, create_time FROM aoe_star_transaction WHERE user_id = ? LIMIT ? OFFSET ?";
            JsonArray data = bridge.query(query, userId, StarConstant.SIZE_DEFAULT, offset);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject addStar(JsonObject data) {
        try {
            int amount = data.get("amount").getAsInt();
            int service = data.get("service").getAsInt();
            String username = data.get("username").getAsString();
            long referId = data.get("referId").getAsLong();
            return balanceTransactionRecord(amount, service, username, referId);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject subStar(JsonObject data) {
        try {
            int amount = -data.get("amount").getAsInt();
            int service = data.get("service").getAsInt();
            String username = data.get("username").getAsString();
            long referId = data.get("referId").getAsLong();
            return balanceTransactionRecord(amount, service, username, referId);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getDetailTransaction(long id) {
        try {
            //get balance transaction
            //get user profile
            return null;
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    private JsonObject balanceTransactionRecord(int amount, int service, String username, long referId) {
        try (Connection connection = HikariClients.instance().defaulSQLJavaBridge().sqlClient.getConnection();
             PreparedStatement stStarWallet = connection.prepareStatement("SELECT * FROM aoe_star WHERE username = ? FOR UPDATE");
             PreparedStatement stUpdateStarWallet = connection.prepareStatement("UPDATE aoe_star SET balance = ? WHERE username = ?");
             PreparedStatement stInsertStarTransaction = connection.prepareStatement("INSERT INTO aoe_star_transaction VALUES(user_id, username, service, refer_id, amount, balance, create_time) WHERE username = ?", Statement.RETURN_GENERATED_KEYS)) {
            connection.setAutoCommit(false);
            stStarWallet.setString(1, username);
            try (ResultSet rs = stStarWallet.executeQuery()) {
                if (rs.next()) {
                    long userId = rs.getLong("user_id");
                    int balance = rs.getInt("balance");
                    int newBalance = balance + amount;
                    if (newBalance < 0) {
                        return BaseResponse.createFullMessageResponse(12, "insufficient_balance");
                    }
                    //update star wallet
                    stUpdateStarWallet.setInt(1, newBalance);
                    stUpdateStarWallet.setString(2, username);
                    int rowUpdateStarWallet = stUpdateStarWallet.executeUpdate();
                    //insert star transaction
                    stInsertStarTransaction.setLong(1, userId);
                    stInsertStarTransaction.setString(2, username);
                    stInsertStarTransaction.setInt(3, service);
                    stInsertStarTransaction.setLong(4, referId);
                    stInsertStarTransaction.setInt(5, amount);
                    stInsertStarTransaction.setInt(6, newBalance);
                    stInsertStarTransaction.setLong(7, System.currentTimeMillis());
                    int rowInsertStarTransaction = stInsertStarTransaction.executeUpdate();
                    if (rowUpdateStarWallet == 0 || rowInsertStarTransaction == 0) {
                        connection.rollback();
                        return BaseResponse.createFullMessageResponse(11, "failure");
                    } else {
                        //get id aoe transaction
                        try (ResultSet result = stInsertStarTransaction.getGeneratedKeys()) {
                            long id = result.getLong(1);
                            //update bank transaction
                        }
                        connection.commit();
                        return BaseResponse.createFullMessageResponse(0, "success");
                    }
                } else {
                    return BaseResponse.createFullMessageResponse(10, "not_found");
                }
            }
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
