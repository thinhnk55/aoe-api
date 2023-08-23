package vn.vietdefi.bank.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.logic.BankTransactionState;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class BankTransactionService implements IBankTransactionService{
    @Override
    public JsonObject createBalanceTransaction(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            bridge.insertObjectToDB("bank_transaction", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    @Override
    public JsonObject getBalanceTransactionById(long id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM bank_transaction WHERE id = ?";
            JsonObject data = bridge.queryOne(query, id);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject listWaitingTransaction() {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM bank_transaction WHERE state = ?";
            JsonArray data = bridge.query(query, BankTransactionState.WAITING);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    @Override
    public void updateBankTransactionState(long id, int state) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE bank_transaction SET state = ? WHERE id = ?";
            bridge.update(query, state, id);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }

    @Override
    public void setStarTransactionId(long id,
                                     long star_transaction_id){
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE bank_transaction SET star_transaction_id = ? WHERE id = ?";
            bridge.update(query, star_transaction_id, id);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }
    @Override
    public void completeBankTransaction(long id, int service,
                                        long target_id){
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE bank_transaction SET state =?, service = ?, target_id = ? WHERE id = ?";
            bridge.update(query, BankTransactionState.DONE, service, target_id, id);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }
}
