package vn.vietdefi.bank.logic.timo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.Response;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.bank.BankServices;
import vn.vietdefi.bank.logic.BankAccount;
import vn.vietdefi.bank.logic.BankAccountState;
import vn.vietdefi.bank.logic.BankController;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

import java.util.HashMap;
import java.util.Map;

public class TimoApi {
    public static void loop(BankAccount account) {
        //Neu login lai thi phai set sang trang thai force update notification
        //Neu truong hop binh thuong thi kiem tra xem co thong bao moi thi moi cap nhat
        boolean updateNotificationFlag = TimoUtil.checkForceUpdateNotification(account);
        if(!updateNotificationFlag){
            int numberOfNotifications = getNumberOfNotification(account);
            if(numberOfNotifications > 0){
                updateNotificationFlag = true;
            }
        }
        if(updateNotificationFlag){
            long lastNotificationId = TimoUtil.getLastNotificationId(account);
            updateNotification(account, lastNotificationId);
        }
    }

    private static void updateNotification(BankAccount account, long lastNotificationId) {
        JsonObject response = getCurrentNotification(account);
        if(!BaseResponse.isSuccessFullMessage(response)){
            if(response.get("error").getAsInt() == 10){
                handleFailure(account);
            }
            return;
        }
        if(BaseResponse.isSuccessFullMessage(response)){
            JsonObject data = response.getAsJsonObject("data");
            JsonArray notificationList = TimoUtil.getNotificationListFromResponse(data);
            if(notificationList.size() == 0){
                return;
            }
            if(lastNotificationId == 0){
                lastNotificationId = notificationList.get(0).getAsJsonObject().get("iD").getAsLong();
                TimoUtil.updateLastNofiticationId(account, lastNotificationId);
                TimoUtil.cancelForceUpdate(account);
                long id = account.bank_detail.get("id").getAsLong();
                JsonObject other = account.bank_detail.getAsJsonObject("other");
                BankServices.timoService.updateOther(id, other);
                return;
            }
            JsonArray notificationToProcess = new JsonArray();
            for(int i = 0; i < notificationList.size(); i++){
                JsonObject json = notificationList.get(i).getAsJsonObject();
                long id = json.get("iD").getAsLong();
                if(id > lastNotificationId){
                    notificationToProcess.add(json);
                }else{
                    break;
                }
            }
            long currentNotificationId = TimoUtil.getNotificationIdFromResponse(data);
            while (currentNotificationId > lastNotificationId){
                response = getNotificationByNotificationId(account, currentNotificationId);
                if(!BaseResponse.isSuccessFullMessage(response)){
                    if(response.get("error").getAsInt() == 10){
                        handleFailure(account);
                    }
                    return;
                }
                data = response.getAsJsonObject("data");
                notificationList = TimoUtil.getNotificationListFromResponse(data);
                for(int i = 0; i < notificationList.size(); i++){
                    JsonObject json = notificationList.get(i).getAsJsonObject();
                    long id = json.get("iD").getAsLong();
                    if(id > lastNotificationId){
                        notificationToProcess.add(json);
                    }else{
                        break;
                    }
                }
                currentNotificationId = TimoUtil.getNotificationIdFromResponse(data);
            }
            processNotificationList(account, notificationToProcess, lastNotificationId);
        }
    }

    private static void processNotificationList(BankAccount account,
                                                JsonArray notificationToProcess,
                                                long lastNotificationId) {
        long last = lastNotificationId;
        for(int i = notificationToProcess.size()-1; i >= 0; i--){
            JsonObject json = notificationToProcess.get(i).getAsJsonObject();
            String group = json.get("group").getAsString();
            if(group.equals("Transfer")) {
                JsonObject transactionInfo = TimoUtil.extractBalanceTransactionFromNotification(json);
                transactionInfo.addProperty("receiver_bankcode", account.bank_code);
                transactionInfo.addProperty("receiver_bank_account", account.account_number);
                transactionInfo.addProperty("receiver_name", account.account_owner);
                transactionInfo.addProperty("sender_bankcode", -1);
                transactionInfo.addProperty("sender_bank_account", "");
                transactionInfo.addProperty("sender_name", "");
                JsonObject response = BankServices.bankTransactionService.createBalanceTransaction(transactionInfo);
                if(BaseResponse.isSuccessFullMessage(response)){
                    last = json.get("iD").getAsLong();
                }else{
                    break;
                }
            }else{
                last = json.get("iD").getAsLong();
            }
        }
        TimoUtil.cancelForceUpdate(account);
        commitReadNotification(account);
        TimoUtil.updateLastNofiticationId(account, last);
        long id = account.bank_detail.get("id").getAsLong();
        JsonObject other = account.bank_detail.getAsJsonObject("other");
        BankServices.timoService.updateOther(id, other);
    }

    private static void commitReadNotification(BankAccount account) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Token", TimoUtil.getToken(account));
        JsonObject payload = new JsonObject();
        payload.addProperty("action", "R");
        payload.addProperty("id", "ALL");
        OkHttpUtil.postJson(TimoConfig.URL_NOTIFICATION_UPDATE, payload.toString(), headers);
    }

    private static JsonObject getCurrentNotification(BankAccount account) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Token", TimoUtil.getToken(account));
            String url = TimoConfig.URL_NOTIFICATION_VN;
            Response response = OkHttpUtil.getFullResponse(url, headers);
            if (response.code() == 200) {
                String data = response.body().string();
                response.body().close();
                JsonObject jsonResponse = GsonUtil.toJsonObject(data);
                return BaseResponse.createFullMessageResponse(0, "success", jsonResponse);
            } else {
                return BaseResponse.createFullMessageResponse(10, "failure");
            }
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    private static JsonObject getNotificationByNotificationId(BankAccount account, long notificationId) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Token", TimoUtil.getToken(account));
            String url = new StringBuilder(TimoConfig.URL_NOTIFICATION_VN)
                    .append("?idIndex=")
                    .append(notificationId).toString();
            Response response = OkHttpUtil.getFullResponse(url, headers);
            if (response.code() == 200) {
                String data = response.body().string();
                response.body().close();
                JsonObject jsonResponse = GsonUtil.toJsonObject(data);
                return BaseResponse.createFullMessageResponse(0, "success", jsonResponse);
            } else {
                return BaseResponse.createFullMessageResponse(10, "failure");
            }
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public static JsonObject login(String username, String password, String device) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("username", username);
            body.addProperty("password", password);
            Map<String, String> headers = new HashMap<>();
            headers.put("x-timo-devicereg", device);
            try (Response response = OkHttpUtil.postFullResponse(TimoConfig.URL_LOGIN, body.toString(), headers)) {
                if (response.code() == 200) {
                    String responseBody = response.body().string();
                    JsonObject res = GsonUtil.toJsonObject(responseBody);
                    if (res.get("code").getAsInt() == 6001) {
                        JsonObject data = res.get("data").getAsJsonObject();
                        return BaseResponse.createFullMessageResponse(TimoConfig.ERROR_LOGIN_TIMO_ACCOUNT_NOT_COMMIT,
                                "not_commit", data);
                    }
                    if (res.get("code").getAsInt() == 200) {
                        JsonObject data = res.get("data").getAsJsonObject();
                        return BaseResponse.createFullMessageResponse(0, "success", data);
                    }
                    if (res.get("code").getAsInt() == 401) {
                        return BaseResponse.createFullMessageResponse(TimoConfig.ERROR_LOGIN_TIMO_ACCOUNT_INVALID,
                                "account_invalid");
                    }
                }
                return BaseResponse.createFullMessageResponse(12, "failure");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public static JsonObject commit(String token, JsonObject body) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("token", token);
            try (Response response = OkHttpUtil.postFullResponse(TimoConfig.URL_LOGIN_COMMIT, body.toString(), headers)) {
                if (response.code() == 200) {
                    String responseBody = response.body().string();
                    JsonObject res = GsonUtil.toJsonObject(responseBody);
                    if (res.get("code").getAsInt() == 8102) {
                        return BaseResponse.createFullMessageResponse(8102, "otp_invalid");
                    }
                    if (res.get("code").getAsInt() == 8106) {
                        return BaseResponse.createFullMessageResponse(8106, "otp_expire");
                    }
                    if (res.get("code").getAsInt() == 200) {
                        JsonObject data = res.get("data").getAsJsonObject();
                        return BaseResponse.createFullMessageResponse(0, "success", data);
                    }
                }
                return BaseResponse.createFullMessageResponse(401, "unauthorized");
            }
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public static JsonObject getBankInfo(String token) {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("token", token);
            try(Response response = OkHttpUtil.getFullResponse(TimoConfig.URL_BANK_INFO, headers)){
                if(response.code() == 200){
                    String responseBody = response.body().string();
                    JsonObject res = GsonUtil.toJsonObject(responseBody);
                    return BaseResponse.createFullMessageResponse(0, "success", res.get("data").getAsJsonObject());
                }
                return BaseResponse.createFullMessageResponse(2, "unauthorized");
            }
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public static int getNumberOfNotification(BankAccount account){
        Map<String, String> headers = new HashMap<>();
        headers.put("Token", TimoUtil.getToken(account));
        Response response = OkHttpUtil.getFullResponse(TimoConfig.URL_NOTIFICATION_CHECK, headers);
        if (response!= null && response.code() == 200) {
            try {
                String data = response.body().string();
                response.body().close();
                JsonObject jsonResponse = GsonUtil.toJsonObject(data);
                //Lay so luong thong bao moi
                int numberOfNotifications = jsonResponse.get("data").getAsJsonObject().get("numberOfNotification").getAsInt();
                return numberOfNotifications;
            }catch (Exception e){
                return -1;
            }
        }else {
            handleFailure(account);
            return -1;
        }
    }


    private static void handleFailure(BankAccount account) {
        BankController.instance().removeBankAccount(account);
        long id = account.bank_detail.get("id").getAsLong();
        JsonObject response = BankServices.timoService.retryLogin(id);
        if(BaseResponse.isSuccessFullMessage(response)){
            BankServices.bankService.updateBankAccountState(account.id, BankAccountState.WORKING);
            BankController.instance().updateWorkingBank();
        }
    }
}
