package vn.vietdefi.bank.logic.timo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.Response;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.BankServices;
import vn.vietdefi.bank.logic.BalanceTransaction;
import vn.vietdefi.bank.logic.BankAccount;
import vn.vietdefi.bank.logic.BankController;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimoApi {
    public static void loop(BankAccount account) {
        int numberOfNotifications = getNumberOfNotification(account);
        if(numberOfNotifications > 0){

        }
    }
    public static String getToken(BankAccount account){
        String token = account.other.get("token").getAsString();
        return token;
    }

    public static int getNumberOfNotification(BankAccount account){
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Token", getToken(account));
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
        BankServices.timoService.retry(account.other);
    }

    public static List<BalanceTransaction> getNotification(long numberOfNotifications, BankAccount account){
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Token", getToken(account));
            String url = TimoConfig.URL_NOTIFICATION_VN;
            Response response = OkHttpUtil.getFullResponse(url, headers);
            if (response.code() == 200) {
                String data = response.body().string();
                JsonObject jsonResponse = GsonUtil.toJsonObject(data);
                JsonArray transactionNotification = new JsonArray();
                long idIndex ;
                while (numberOfNotifications > 0) {
                    idIndex = jsonResponse.getAsJsonObject("data").get("idIndex").getAsLong();
                    JsonArray notifications = jsonResponse.getAsJsonObject("data").getAsJsonArray("notifyList");
                    for (JsonElement notification : notifications) {
                        JsonObject notificationObj = notification.getAsJsonObject();
                        boolean isRead = notificationObj.get("read").getAsBoolean();
                        String group = notificationObj.get("group").getAsString();
                        if (!isRead && group.equals("Transfer")) {
                            String deeplink = TimoUtil.extractRefNoFromUrl(notificationObj.get("deeplink").getAsString());
                            String accountBalance = TimoUtil.extractAccountBalance(notificationObj.get("content").getAsString());
                            JsonObject auth = new JsonObject();
                            auth.addProperty("Token",token);
                            auth.addProperty("refNo",deeplink);
                            auth.addProperty("balance",accountBalance);
                            auth.addProperty("error",jsonResponse.getAsJsonObject().get("code").getAsString());
                            BalanceTransaction balance = getTransaction(auth, account);
                            // Decrease the count of remaining notifications
                        }else {
                            break;
                        }
                    }
                    String apiUrl = url + "?idIndex=" + idIndex;
                    response.close();
                    response = OkHttpUtil.getFullResponse(apiUrl, headers);
                    if(response.code() == 200){
                        data = response.body().string();
                        jsonResponse = GsonUtil.toJsonObject(data);
                        numberOfNotifications = numberOfNotifications - 20;
                    }else{
                        handleFailure(account);
                    }
                }
                return listTransaction;
            }else{
                handleFailure(account);
                return null;
            }
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}
