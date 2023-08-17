package vn.vietdefi.bank.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.BankServices;
import vn.vietdefi.bank.logic.BankAccountState;
import vn.vietdefi.bank.logic.BankCode;
import vn.vietdefi.bank.logic.timo.TimoApi;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class BankService implements IBankService {
    @Override
    public JsonObject createBankAccount(int bankCode, String accountOwner, String accountNumber, long bankDetailId) {
        try {
            JsonObject response = getBankAccount(bankCode, accountNumber);
            if(BaseResponse.isSuccessFullMessage(response)){
                return response;
            }
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject data = new JsonObject();
            data.addProperty("bank_code", bankCode);
            data.addProperty("account_number", accountNumber);
            data.addProperty("account_owner", accountOwner);
            data.addProperty("bank_detail_id", bankDetailId);
            data.addProperty("state", BankAccountState.ACTIVE);
            bridge.insertObjectToDB("bank_account", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject getBankAccount(int bankCode, String accountNumber) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM bank_account WHERE bank_code = ? AND account_number = ?";
            JsonObject data = bridge.queryOne(query, bankCode, accountNumber);
            if(data == null){
                return BaseResponse.createFullMessageResponse(1, "bank_account_not_found");
            }else{
                return BaseResponse.createFullMessageResponse(0, "success", data);
            }
        } catch (Exception e) {
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getWorkingBanks() {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM bank_account WHERE state = ?";
            JsonArray data = bridge.query(query, BankAccountState.WORKING);
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
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            bridge.insertObjectToDB("bank_transaction", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject createBankAccountFromTimoAccount(JsonObject data) {
        try {
            long bank_account_id = data.get("bank_account_id").getAsLong();
            if(bank_account_id == 0){
                String token = data.get("token").getAsString();
                JsonObject response = TimoApi.getBankInfo(token);
                if(BaseResponse.isSuccessFullMessage(response)){
                    JsonObject bankInfo = response.getAsJsonObject("data");
                    String accountNumber = bankInfo.get("accountNumber").getAsString();
                    String accountOwner = bankInfo.get("fullName").getAsString();
                    long id = data.get("id").getAsLong();
                    response = createBankAccount(BankCode.TIMO,
                            accountOwner, accountNumber, id);
                    if(BaseResponse.isSuccessFullMessage(response)) {
                        bank_account_id = response.getAsJsonObject("data").get("id").getAsLong();
                        BankServices.timoService.updateBankAccountId(id, bank_account_id);
                    }
                    return response;
                }else{
                    return BaseResponse.createFullMessageResponse(10, "api_failure");
                }
            }else{
                updateBankState(bank_account_id, BankAccountState.ACTIVE);
                return getAccountById(bank_account_id);
            }
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getAccountById(long id) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM bank_account WHERE id = ?";
            JsonObject data = bridge.queryOne(query, id);
            if(data == null){
                return BaseResponse.createFullMessageResponse(10, "bank_account_not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
