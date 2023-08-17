package vn.vietdefi.bank.services.timo;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.logic.BankCode;
import vn.vietdefi.bank.logic.timo.TimoApi;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;


public class TimoService implements ITimoService {
    @Override
    public JsonObject loginTimo(String username, String password) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject resTimo = TimoApi.login(username, password);
            int error = resTimo.get("error").getAsInt();
            if (error == 401) {
                return resTimo;
            } else if (error == 200) {
                return BaseResponse.createFullMessageResponse(1001, "account_already");
            }
            //response
            String token = resTimo.get("data").getAsJsonObject().get("token").getAsString();
            String refNo = resTimo.get("data").getAsJsonObject().get("refNo").getAsString();
            JsonObject response = new JsonObject();
            response.addProperty("token", token);
            response.addProperty("refNo", refNo);
            //insert timo account
            String query = "INSERT INTO timo_account(username,password,token) VALUES(?,?,?)";
            int row = bridge.update(query, username, password, token);
            if (row == 0) {
                return BaseResponse.createFullMessageResponse(2, "unresolved");
            }
            return BaseResponse.createFullMessageResponse(0, "success",resTimo);
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject commitTimo(String token, String refNo, String otp) {
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
            String query = "UPDATE timo_account SET state = 1, token = ? WHERE token = ?";
            bridge.update(query, newToken, token);
            //call bank info
            resTimo = TimoApi.bankInfo(newToken);
            String accountNumber = resTimo.get("data").getAsJsonObject().get("accountNumber").getAsString();
            String accountOwner = resTimo.get("data").getAsJsonObject().get("fullName").getAsString();
            //update bank account
            query = "INSERT INTO bank_account(bank_code,account_number,account_owner) VALUES(?,?,?)";
            bridge.update(query, BankCode.TIMO, accountNumber, accountOwner);
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
}
