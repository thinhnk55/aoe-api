package vn.vietdefi.bank.logic.timo;

import vn.vietdefi.bank.logic.BankAccount;
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
        String token = account.other.get("token").getAsString();
        return token;
    }

    public static String extractRefNoFromUrl(String url) {
        String pattern = "refNo=([^&]*)"; // Pattern to match "refNo=" followed by any characters except "&"
        Pattern refNoPattern = Pattern.compile(pattern);
        Matcher matcher = refNoPattern.matcher(url);

        if (matcher.find()) {
            return matcher.group(1); // Extract the captured group
        }

        return null; // Return null if "refNo=" is not found in the URL
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
