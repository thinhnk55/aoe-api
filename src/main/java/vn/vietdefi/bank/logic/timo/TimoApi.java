package vn.vietdefi.bank.logic.timo;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.Response;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.logic.BalanceTransaction;
import vn.vietdefi.bank.logic.BankAccount;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static vn.vietdefi.bank.logic.timo.TimoUtil.*;

public class TimoApi {
    public static JsonObject login(JsonObject auth) {
        try {
            String username = auth.get("username").getAsString();
            String password = auth.get("password").getAsString();
            JsonObject body = new JsonObject();
            body.addProperty("username", username);
            body.addProperty("password", password);
            Map<String, String> headers = new HashMap<>();
            headers.put("x-timo-devicereg", TimoConfig.xTimoDevice);
            try (Response response = OkHttpUtil.postFullResponse(TimoConfig.URL_LOGIN, body.toString(), headers)) {
                if (response.code() == 200) {
                    String responseBody = response.body().string();
                    JsonObject res = GsonUtil.toJsonObject(responseBody);
                    if (res.get("code").getAsInt() == 6001) {
                        JsonObject data = res.get("data").getAsJsonObject();
                        return BaseResponse.createFullMessageResponse(6001, "not_commit", data);
                    }
                    if (res.get("code").getAsInt() == 200) {
                        JsonObject data = res.get("data").getAsJsonObject();
                        return BaseResponse.createFullMessageResponse(200, "login_success", data);
                    }
                    if (res.get("code").getAsInt() == 401) {
                        return BaseResponse.createFullMessageResponse(401, "account_invalid");
                    }
                }
                return BaseResponse.createFullMessageResponse(1, "system_error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public static List<BalanceTransaction> update(BankAccount account){
        try {
            if(account == null){
                return null;
            }
            Map<String, String> headers = new HashMap<>();
            String token = account.other.get("token").getAsString();
            headers.put("Token", token);
            Response response = OkHttpUtil.getFullResponse(TimoConfig.URL_NOTIFICATION_CHECK, headers);
            if (response.code() == 200) {
                String data = response.body().string();
                response.body().close();
                JsonObject jsonResponse = GsonUtil.toJsonObject(data);
                int numberOfNotifications = jsonResponse.get("data").getAsJsonObject().get("numberOfNotification").getAsInt(); // lấy số lượng thông báo mới
                List<BalanceTransaction> transactionNotification = getNotification(token,numberOfNotifications,account);
                return transactionNotification;
            }else {
                TestBankService.bankService.updateStateAccount(1, account);
                //notification telegram
                VdefServices.telegramService.sendMessage(Constants.TELEGRAM_SUPER_ADMIN_ID, String.format("Your bank account: %s - %s - %s has been logged out!",account.getBankName(), account.getAccountNumber(), account.getAccountOwner()));
            }
            DebugLogger.info("{} {}", response.code(), response.body());
            return null;
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }


    public static List<BalanceTransaction> getNotification(String token , long numberOfNotifications, BankAccount account){
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Token", token);
            String url = TimoConfig.URL_NOTIFICATION_VN;
            List<BalanceTransaction> listTransaction = new ArrayList<>();


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
                            String deeplink = getTokenTimo()extractRefNoFromUrl(notificationObj.get("deeplink").getAsString());
                            String accountBalance = TimoUtil.extractAccountBalance(notificationObj.get("content").getAsString());
                            JsonObject auth = new JsonObject();
                            auth.addProperty("Token",token);
                            auth.addProperty("refNo",deeplink);
                            auth.addProperty("balance",accountBalance);
                            auth.addProperty("error",jsonResponse.getAsJsonObject().get("code").getAsString());

                            BalanceTransaction balance = getTransaction(auth, account);
                            listTransaction.add(balance);
                            // Decrease the count of remaining notifications
                        }else {
                            break;
                        }
                    }
                    String apiUrl = url + "?idIndex=" + idIndex;
                    response.close();
                    response = OkHttpUtil.getFullResponse(apiUrl, headers);
                    data = response.body().string();
                    jsonResponse = GsonUtil.toJsonObject(data);
                    numberOfNotifications = numberOfNotifications - 20;
                }
                DebugLogger.info("{} {}", response.code(), response.body());
                return listTransaction;
            }
            else{
                DebugLogger.info("{} {}", response.code(), response.body());
                TestBankService.bankService.updateStateAccount(1, account);
                //notification telegram
                VdefServices.telegramService.sendMessage(Constants.TELEGRAM_SUPER_ADMIN_ID, String.format("Your bank account: %s - %s - %s has been logged out!",account.getBankName(), account.getAccountNumber(), account.getAccountOwner()));
                return null;
            }
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}
