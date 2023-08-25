package vn.vietdefi.bank.services.timo;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.BankServices;
import vn.vietdefi.bank.logic.BankCode;
import vn.vietdefi.bank.logic.BankController;
import vn.vietdefi.bank.logic.timo.TimoApi;
import vn.vietdefi.bank.logic.timo.TimoConfig;
import vn.vietdefi.bank.logic.timo.TimoUtil;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;
import vn.vietdefi.util.string.StringUtil;

public class TimoService implements ITimoService {
    @Override
    public JsonObject loginTimo(String username, String password) {
        try {
            String hashedPassword = StringUtil.sha512(password);
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject response = getAccountByUsername(username);
            JsonObject data;
            String device;
            if(!BaseResponse.isSuccessFullMessage(response)){
                data = new JsonObject();
                data.addProperty("username", username);
                data.addProperty("password", password);
                device = TimoUtil.generateRandomTimoDevice();
                data.addProperty("device", device);
                data.add("other", createOther());
                bridge.insertObjectToDB("bank_timo_account", data);
            }else{
                data = response.getAsJsonObject("data");
                device = data.get("device").getAsString();
            }
            response = TimoApi.login(username, hashedPassword, device);
            DebugLogger.info("TimoApi.login {}", response);
            if(BaseResponse.isSuccessFullMessage(response)){
                JsonObject timoResponse = response.getAsJsonObject("data");
                String token = timoResponse.get("token").getAsString();
                data.addProperty("token", token);
                JsonObject other = data.getAsJsonObject("other");
                other.addProperty("force_update_notification", true);
                long id = data.get("id").getAsLong();
                String query = "UPDATE bank_timo_account SET password = ?, token = ?, other = ?, state = ? WHERE id = ?";
                bridge.update(query, password, token, other, TimoAccountState.COMMIT, id);
                return BaseResponse.createFullMessageResponse(0, "success", data);
            }else if(response.get("error").getAsInt() == TimoConfig.ERROR_LOGIN_TIMO_ACCOUNT_NOT_COMMIT){
                JsonObject timoResponse = response.getAsJsonObject("data");
                String refNo = timoResponse.get("refNo").getAsString();
                String token = timoResponse.get("token").getAsString();
                JsonObject other = data.getAsJsonObject("other");
                other.addProperty("login_refNo", refNo);
                long id = data.get("id").getAsLong();
                String query = "UPDATE bank_timo_account SET password = ?, token = ?, other = ?, state = ? WHERE id = ?";
                bridge.update(query, password, token, other, TimoAccountState.NOT_COMMIT, id);
                return BaseResponse.createFullMessageResponse(10, "require_otp", data);
            }else{
                return BaseResponse.createFullMessageResponse(11, "login_failed");
            }
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    private JsonObject createOther() {
        JsonObject other = new JsonObject();
        other.addProperty("last_notification_id", 0);
        other.addProperty("force_update_notification", false);
        return other;
    }

    @Override
    public JsonObject commitTimo(long id, String otp) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject response = getAccountById(id);
            if(!BaseResponse.isSuccessFullMessage(response)){
                return BaseResponse.createFullMessageResponse(10, "timo_account_not_found");
            }
            JsonObject data = response.getAsJsonObject("data");
            String refNo = data.getAsJsonObject("other").get("login_refNo").getAsString();
            String token = data.get("token").getAsString();
            //payload
            JsonObject body = new JsonObject();
            body.addProperty("refNo", refNo);
            body.addProperty("otp", otp);
            body.addProperty("securityChallenge", otp);
            body.addProperty("securityCode", otp);
            //commit otp
            JsonObject timoResponse = TimoApi.commit(token, body);
            if (!BaseResponse.isSuccessFullMessage(timoResponse)) {
                return BaseResponse.createFullMessageResponse(11, "otp_invalid");
            }
            String newToken = timoResponse.getAsJsonObject("data").get("token").getAsString();
            data.addProperty("token", newToken);
            JsonObject other = data.getAsJsonObject("other");
            if(other == null){
                other = createOther();
                data.add("other", other);
            }else{
                other.addProperty("force_update_notification", true);
            }
            data.addProperty("state", TimoAccountState.COMMIT);
            String query = "UPDATE bank_timo_account SET token = ?, other = ?, state = ? WHERE id = ?";
            bridge.update(query, newToken, other, TimoAccountState.COMMIT, id);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    @Override
    public JsonObject updateToken(long id, String token) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE bank_timo_account SET token = ? WHERE id = ?";
            bridge.update(query, token, id);
            return BaseResponse.createFullMessageResponse(0, "success");
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
            String query = "SELECT * FROM bank_timo_account WHERE id = ?";
            JsonObject data = bridge.queryOne(query, id);
            if(data == null){
                return BaseResponse.createFullMessageResponse(10, "timo_account_not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getAccountByUsername(String username) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM bank_timo_account WHERE username = ?";
            JsonObject data = bridge.queryOne(query, username);
            if(data == null){
                return BaseResponse.createFullMessageResponse(10, "timo_account_not_found");
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception exception) {
            String stacktrace = ExceptionUtils.getStackTrace(exception);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject retryLogin(long id) {
        JsonObject response = getAccountById(id);
        if(!BaseResponse.isSuccessFullMessage(response)){
            return response;
        }
        JsonObject data = response.getAsJsonObject("data");
        String username = data.get("username").getAsString();
        String password = data.get("password").getAsString();
        response = loginTimo(username, password);
        return response;
    }

    @Override
    public void updateOther(long id, JsonObject other) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE bank_timo_account SET other = ? WHERE id = ?";
            bridge.update(query, other, id);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }

    @Override
    public void updateBankAccountId(long id, long bankAccountId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE bank_timo_account SET bank_account_id = ? WHERE id = ?";
            bridge.update(query, bankAccountId, id);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }

    @Override
    public JsonObject getBankAccountInfo(JsonObject data) {
        String token = data.get("token").getAsString();
        JsonObject response = TimoApi.getBankInfo(token);
        if (BaseResponse.isSuccessFullMessage(response)) {
            JsonObject bankInfo = response.getAsJsonObject("data");
            return BaseResponse.createFullMessageResponse(0, "success", bankInfo);
        }else{
            return BaseResponse.createFullMessageResponse(10, "failure");
        }
    }
}
