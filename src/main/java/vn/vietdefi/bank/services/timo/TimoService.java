package vn.vietdefi.bank.services.timo;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.ApiBank;
import vn.vietdefi.bank.logic.BankAccountState;
import vn.vietdefi.bank.logic.BankCode;
import vn.vietdefi.bank.logic.timo.TimoApi;
import vn.vietdefi.bank.logic.timo.TimoConfig;
import vn.vietdefi.bank.logic.timo.TimoUtil;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;


public class TimoService implements ITimoService {
    @Override
    public JsonObject loginTimo(String username, String password) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String device = TimoUtil.generateRandomTimoDevice();
            JsonObject resTimo = TimoApi.login(username, password, device);
            int error = resTimo.get("error").getAsInt();
            if (error == 401) {
                return resTimo;
            } else if (error == 200) {
                return BaseResponse.createFullMessageResponse(1001, "account_already");
            }
            //get data timo
            String token = resTimo.get("data").getAsJsonObject().get("token").getAsString();
            //insert timo account
            JsonObject timoAccount = new JsonObject();
            timoAccount.addProperty("username", username);
            timoAccount.addProperty("password", password);
            timoAccount.addProperty("token", token);
            timoAccount.addProperty("device", device);
            bridge.insertObjectToDB("timo_account", timoAccount);
            //response
            JsonObject response = resTimo.get("data").getAsJsonObject();
            response.addProperty("timo_id", timoAccount.get("id").getAsLong());
            return BaseResponse.createFullMessageResponse(0, "success", response);
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject commitTimo(String token, String refNo, String otp, long timoId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            //payload
            JsonObject body = new JsonObject();
            body.addProperty("refNo", refNo);
            body.addProperty("otp", otp);
            body.addProperty("securityChallenge", otp);
            body.addProperty("securityCode", otp);
            //commit otp
            JsonObject resTimo = TimoApi.commit(token, body);
            if (!BaseResponse.isSuccessFullMessage(resTimo)) {
                return resTimo;
            }
            //get new token and update state
            String newToken = resTimo.get("data").getAsJsonObject().get("token").getAsString();
            //update timo account
            String query = "UPDATE timo_account SET state = 1, token = ? WHERE id = ?";
            bridge.update(query, newToken, timoId);
            //call bank info
            resTimo = TimoApi.bankInfo(newToken);
            String accountNumber = resTimo.get("data").getAsJsonObject().get("accountNumber").getAsString();
            String accountOwner = resTimo.get("data").getAsJsonObject().get("fullName").getAsString();
            //create bank account
            JsonObject bankAccount = new JsonObject();
            bankAccount.addProperty("bank_code", BankCode.TIMO);
            bankAccount.addProperty("account_number", accountNumber);
            bankAccount.addProperty("account_owner", accountOwner);
            bankAccount.addProperty("state", BankAccountState.ACTIVE);
            bankAccount.addProperty("bank_detail_id", timoId);
            JsonObject resBank = ApiBank.bankService.createBankAccount(bankAccount);
            if(BaseResponse.isSuccessFullMessage(resBank)){
                long bankAccountId = resBank.get("data").getAsJsonObject().get("id").getAsLong();
                query = "UPDATE timo_account SET bank_account_id = ? WHERE id = ?";
                bridge.update(query, bankAccountId, timoId);
            }
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getAccountById(long id) {
        return null;
    }

    @Override
    public void retryLogin(JsonObject other) {

    }

    @Override
    public void updateOther(JsonObject other) {

    }
}
