package vn.vietdefi.bank.vertx;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.BankServices;
import vn.vietdefi.bank.logic.BankController;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class BankRouter {
    public static void login(RoutingContext rc) {
        try {
            String request = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            int bankCode = Integer.parseInt(data.get("bank_code").getAsString());
            String username = data.get("username").getAsString();
            String password = data.get("password").getAsString();
            JsonObject response = BankServices.bankService.login(bankCode, username, password);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }

    public static void commitOTP(RoutingContext rc) {
        try {
            String request = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            int bankCode = Integer.parseInt(data.get("bank_code").getAsString());
            long id = Long.parseLong(data.get("id").getAsString());
            String otp = data.get("otp").getAsString();
            JsonObject response = BankServices.bankService.commitOTP(bankCode, id, otp);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }

    public static void getListBankByState(RoutingContext routingContext) {
        try {
            int page = Integer.parseInt(routingContext.request().getParam("page","1"));
            int state = Integer.parseInt(routingContext.request().getParam("state"));
            JsonObject response = BankServices.bankService.listBankAccountByState(state, page, 20);
            routingContext.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            routingContext.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }

    public static void startWorkingBank(RoutingContext rc) {
        try {
            String request = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            long id = Long.parseLong(data.get("id").getAsString());
            JsonObject response = BankServices.bankService.startWorking(id);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }

    public static void waitToWorkBank(RoutingContext rc) {
        try {
            String request = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            long id = Long.parseLong(data.get("id").getAsString());
            JsonObject response = BankServices.bankService.waitToWork(id);
            BankController.instance().updateWorkingBank();
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }

    public static void disableBank(RoutingContext rc) {
        try {
            String request = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            long id = Long.parseLong(data.get("id").getAsString());
            JsonObject response = BankServices.bankService.disable(id);
            BankController.instance().updateWorkingBank();
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }

    public static void getWorkingBank(RoutingContext rc) {
        try {
            JsonObject response = BankServices.bankService.getOneWorkingBankAccount();
            BankController.instance().updateWorkingBank();
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }
}
