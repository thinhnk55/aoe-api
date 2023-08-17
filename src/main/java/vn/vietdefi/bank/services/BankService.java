package vn.vietdefi.bank.services;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.logic.BalanceTransaction;
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
        return null;
    }

    @Override
    public JsonObject updateBankState(long id, int state) {
        return null;
    }

    @Override
    public JsonObject createBalanceTransaction(JsonObject data) {
        //Neu ton tai transaction roi thi van tra ve thanh cong
        return null;
    }
}
