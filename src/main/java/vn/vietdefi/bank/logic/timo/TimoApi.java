package vn.vietdefi.bank.logic.timo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.Response;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.BankServices;
import vn.vietdefi.bank.logic.BankAccount;
import vn.vietdefi.bank.logic.BankController;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

import java.util.HashMap;
import java.util.Map;

import static vn.vietdefi.bank.logic.timo.TimoUtil.*;

public class TimoApi {
    public static void loop(BankAccount account) {
        int numberOfNotifications = getNumberOfNotification(account);
        if(numberOfNotifications > 0){

        }
    }

    public static int getNumberOfNotification(BankAccount account){
        try {
            if(account == null){
                return null;
            }
            Map<String, String> headers = new HashMap<>();
            headers.put("Token", TimoUtil.getToken(account));
            Response response = OkHttpUtil.getFullResponse(TimoConfig.URL_NOTIFICATION_CHECK, headers);
            if (response.code() == 200) {
                String data = response.body().string();
                response.body().close();
                JsonObject jsonResponse = GsonUtil.toJsonObject(data);
                //Lay so luong thong bao moi
                int numberOfNotifications = jsonResponse.get("data").getAsJsonObject().get("numberOfNotification").getAsInt();
                return numberOfNotifications;
            }else {
                handleFailure(account);
                return -1;
            }
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return -1;
        }
    }

    private static void handleFailure(BankAccount account) {
        //Dua ra khoi vong loop
        BankController.instance().removeBankAccount(account);
        //Thong bao cho admin
        //Thong bao cho Timo Service de retry
        BankServices.timoService.retryLogin(account.other);
    }
}
