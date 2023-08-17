package vn.vietdefi.bank.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.logic.BalanceTransaction;
import vn.vietdefi.bank.logic.BankAccountState;
import vn.vietdefi.bank.logic.BankCode;
import vn.vietdefi.bank.services.timo.ITimoService;
import vn.vietdefi.bank.services.timo.TimoService;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

import java.util.List;

public class BankService implements IBankService {
    private final ITimoService timoService = new TimoService();

    @Override
    public JsonObject login(JsonObject data) {
        try {
            int bankCode = data.get("bank_code").getAsInt();
            String username = data.get("username").getAsString();
            String password = data.get("password").getAsString();
            switch (bankCode) {
                case BankCode.TIMO:
                    return timoService.loginTimo(username, password);
            }
            return BaseResponse.createFullMessageResponse(10, "bank_un_support");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject commit(JsonObject data) {
        try {
            int bankCode = data.get("bank_code").getAsInt();
            String otp = data.get("otp").getAsString();
            String token = data.get("token").getAsString();
            switch (bankCode) {
                case BankCode.TIMO:
                    String refNo = data.get("refNo").getAsString();
                    long timoId = data.get("timoId").getAsLong();
                    return timoService.commitTimo(token, refNo, otp, timoId);
            }
            return BaseResponse.createFullMessageResponse(10, "bank_un_support");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject createBankAccount(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            bridge.insertObjectToDB("bank_account", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }

    }

    @Override
    public JsonObject getActiveBanks() {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM bank_account WHERE state = ?";
            JsonArray data = bridge.query(query, BankAccountState.WORKING);
            if (data.isEmpty()) {
                query = "SELECT * FROM bank_account WHERE state = ?";
                JsonObject nextAccount = bridge.queryOne(query,BankAccountState.ACTIVE);
                JsonObject authAccount = login(timoService.getInfoLogin(nextAccount.get("bank_account_id").getAsLong()));
                if (authAccount.get("error").getAsInt() == 200) {
                    // update state + token
                    timoService.updateTokenBank(authAccount.get("data").getAsJsonObject(), nextAccount.get("id").getAsInt());
                    nextAccount.addProperty("other",authAccount.get("data").getAsJsonObject().get("token").getAsString());
                    timoService.getMissNotification(nextAccount);
                } else {
                    long id = nextAccount.get("id").getAsLong();
                    updateBankState(id,BankAccountState.DISABLE);
                }
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public void updateBankState(long id, int state) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE bank_account SET state = ? WHERE id = ?";
            bridge.update(query, state, id);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }
    
    @Override
    public JsonObject createBalanceTransaction(JsonObject data) {
        //Neu ton tai transaction roi thi van tra ve thanh cong
        return null;
    }
}
