package vn.vietdefi.aoe.services.bank;

import com.google.gson.JsonObject;
import vn.vietdefi.bank.logic.BankCode;
import vn.vietdefi.bank.logic.BankTransaction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AoeBankMessage {
    String sender;
    String action;
    int targetType;
    String targetId;

    public AoeBankMessage(String sender, String action, int targetType, String targetId) {
        this.sender = sender;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
    }

    public static AoeBankMessage createFromBalanceTransaction(BankTransaction transaction) {
        if (transaction.getReceiverBankCode() == BankCode.TIMO) {
            return timoConvert(transaction);
        }
        return null;
    }

    private static AoeBankMessage timoConvert(BankTransaction transaction) {
        String data = transaction.getNote();
        // Biểu thức chính quy
        String regex = "(\\d+) (donate) (\\w+\\d+)";
        String regex2 = "(\\d+) (donate star)";
        // Tạo Pattern từ biểu thức chính quy
        Pattern pattern = Pattern.compile(regex);
        Pattern pattern2 = Pattern.compile(regex2);
        // Tạo đối tượng Matcher để tìm kiếm
        Matcher matcher = pattern.matcher(data);
        Matcher matcher2 = pattern2.matcher(data);
        // Trích xuất thông tin
        String phoneNumber = "";
        String role = "";
        int targetId = 0; // 1: nạp tienn 2: donate match 3 donate gamer 4:donate BLV
        String targetReceiver = "";
        if (matcher.find()) {
            phoneNumber = matcher.group(1);
            role = matcher.group(2);
            targetReceiver = matcher.group(3).trim();
            JsonObject target = convertTarget(targetReceiver);
            if(target != null) {
                String targetCode = target.get("code").getAsString();
                if(targetCode.equals("KD")) {
                    targetId = 2;
                } else if (targetCode.equals("GT")) {
                    targetId = 3;
                }else if (targetCode.equals("BL"))
                {
                    targetId = 4;
                }
                targetReceiver = target.get("id").getAsString();
                return new AoeBankMessage(phoneNumber, role, targetId, targetReceiver);
            }
            else return null;
        } else if (matcher2.find()) {
            phoneNumber = matcher2.group(1);
            role = matcher2.group(2);
            return new AoeBankMessage(phoneNumber, role, 1,phoneNumber );
        }
        return null;
    }

    private static JsonObject convertTarget(String target) {
        String regex = "([A-Za-z]+)(\\d+)";

        Pattern pattern = Pattern.compile(regex);
        JsonObject targetJson = new JsonObject();
        Matcher matcher1 = pattern.matcher(target);
        if (matcher1.find()) {
            String prefix = matcher1.group(1);
            String number = matcher1.group(2);
            targetJson.addProperty("code",prefix);
            targetJson.addProperty("id",number);
        }else {
            return null;
        }

        return targetJson;
    }
}
