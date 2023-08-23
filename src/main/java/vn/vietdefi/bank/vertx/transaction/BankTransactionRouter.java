package vn.vietdefi.bank.vertx.transaction;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.BankServices;
import vn.vietdefi.bank.logic.BankController;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class BankTransactionRouter {

    public static void createBankTransaction(RoutingContext rc) {
        try {
            String request = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            JsonObject response = BankServices.bankTransactionService.createBalanceTransaction(data);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }

    public static void fixTransaction(RoutingContext rc) {
        try {
            String request = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(request);
            long id = Long.parseLong(data.get("id").getAsString());
            String note = data.get("note").getAsString();
            JsonObject response = BankServices.bankTransactionService.fixTransaction(id, note);
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }

    public static void listBankTransactionError(RoutingContext rc) {
        try {
            JsonObject response = BankServices.bankTransactionService.listBankTransactionError();
            rc.response().end(response.toString());
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1, "system_error").toString());
        }
    }
}
