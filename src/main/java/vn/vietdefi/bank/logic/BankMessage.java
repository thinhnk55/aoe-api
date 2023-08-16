package vn.vietdefi.bank.logic;

import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankMessage {
    String sender;
    String action;
    int targetType;
    String targetId;

    public BankMessage(String sender, String action, int targetType, String targetId) {
        this.sender = sender;
        this.action = action;
        this.targetType = targetType;
        this.targetId = targetId;
    }

    public static BankMessage convert(BalanceTransaction transaction) {
        if (transaction.getReceiverBankCode() == BankCode.TIMO) {
            return timoConvert(transaction);
        }
        return null;
    }

    private static BankMessage timoConvert(BalanceTransaction transaction) {
        String data = transaction.getNote();

        // Biểu thức chính quy
        String regex = "(\\d{10}) (Donate) (\\w+\\d+)";
        String regex2 = "(\\d{10}) (nap tien)";


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
                }else{
                    targetId = 4;
                }
                targetReceiver = target.get("id").getAsString();

                return new BankMessage(phoneNumber, role, targetId, targetReceiver);
            }
            else return null;
        } else if (matcher2.find()) {
            phoneNumber = matcher2.group(1);
            role = matcher2.group(2);
            return new BankMessage(phoneNumber, role, 1,phoneNumber );
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
