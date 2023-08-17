package vn.vietdefi.bank.logic.timo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import vn.vietdefi.bank.logic.BankAccount;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.string.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimoUtil {
    public static String generateRandomTimoDevice(){
        String uuid = StringUtil.generateUUID();
        String client = ":WEB:WEB:176:WEB:desktop:chrome";
        String deviceId = new StringBuilder(uuid).append(client).toString();
        return deviceId;
    }

    public static String getToken(BankAccount account){
        String token = account.bank_detail.get("token").getAsString();
        return token;
    }

    public static long getLastNotificationId(BankAccount account){
        JsonObject other = account.bank_detail.getAsJsonObject("other");
        return other.get("last_notification_id").getAsLong();
    }

    public static void updateLastNofiticationId(BankAccount account, long last) {
        JsonObject other = account.bank_detail.getAsJsonObject("other");
        other.addProperty("last_notification_id", last);
    }
    public static void cancelForceUpdate(BankAccount account) {
        JsonObject other = account.bank_detail.getAsJsonObject("other");
        other.addProperty("force_update_notification", false);
    }
    public static boolean checkForceUpdateNotification(BankAccount account){
        JsonObject other = account.bank_detail.getAsJsonObject("other");
        return other.get("force_update_notification").getAsBoolean();
    }
    public static long getNotificationIdFromResponse(JsonObject data) {
        long id = data.getAsJsonObject("data")
                .get("idIndex").getAsLong();
        return id;
    }
    public static JsonArray getNotificationListFromResponse(JsonObject data) {
        JsonArray array = data.getAsJsonObject("data")
                .get("notifyList").getAsJsonArray();
        return array;
    }

    public static JsonObject extractBalanceTransactionFromNotification(JsonObject data) {
        JsonObject transactionInfo = new JsonObject();
        String fullContent = data.get("content").getAsString();
        String[] lines = fullContent.split("\n");
        String description = lines[2];
        String note = description.substring(10);
        transactionInfo.addProperty("note", note);

        String deeplink = data.get("deeplink").getAsString();
        String bank_transaction_id = deeplink.substring(deeplink.length()-17, deeplink.length()-1);
        transactionInfo.addProperty("bank_transaction_id", bank_transaction_id);

        String regex = "\\d+([.]\\d+)*";
        Pattern accountBalancePattern = Pattern.compile(regex);
        Matcher matcher = accountBalancePattern.matcher(lines[0]);
        long amount = 0;
        if (matcher.find()) {
            String money = matcher.group(0).replaceAll(".","");
            amount = Long.parseLong(money);
        }
        transactionInfo.addProperty("amount", amount);

        return transactionInfo;
    }

    public static String extractAccountBalance(String input) {
        String pattern = "Số dư hiện tại: ([0-9., ]+)"; // Pattern to match "Số dư hiện tại:" followed by any digits, commas, dots, or spaces
        Pattern accountBalancePattern = Pattern.compile(pattern);
        Matcher matcher = accountBalancePattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null; // Return null if pattern is not found in the input
    }


}
