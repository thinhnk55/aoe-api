package vn.vietdefi.aoe.services.bank;

import com.google.gson.JsonObject;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.bank.logic.BankCode;
import vn.vietdefi.bank.logic.BankTransaction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AoeBankAction {
    String sender;
    int service;
    long receiverId;

    public AoeBankAction(String sender, int service,long receiverId) {
        this.sender = sender;
        this.service = service;
        this.receiverId = receiverId;
    }

    public AoeBankAction(String sender, int service) {
        this.sender = sender;
        this.service = service;
    }


    public static AoeBankAction createFromBalanceTransaction(BankTransaction transaction) {
        if (transaction.getReceiverBankCode() == BankCode.TIMO) {
            return timoConvert(transaction);
        }
        return null;
    }

    private static AoeBankAction timoConvert(BankTransaction transaction) {
        String data = transaction.getNote();
        // Biểu thức chính quy
        String regex = new StringBuilder( "(\\d+) ").append((AoeBankConstant.MESSAGE_DONATE)).append(" (\\w+\\d+)").toString();
        String regex2 = new StringBuilder("(\\d+) ").append(AoeBankConstant.MESSAGE_RECHARGE).toString();
        // Tạo Pattern từ biểu thức chính quy
        Pattern pattern = Pattern.compile(regex);
        Pattern pattern2 = Pattern.compile(regex2);
        // Tạo đối tượng Matcher để tìm kiếm
        Matcher matcher = pattern.matcher(data);
        Matcher matcher2 = pattern2.matcher(data);
        // Trích xuất thông tin
        String phoneNumber = "";
        int service = 0;
        String targetReceiver = "";
        if (matcher.find()) {
            phoneNumber = matcher.group(1);
            targetReceiver = matcher.group(3).trim();
            JsonObject target = convertTarget(targetReceiver);
            if(target != null) {
                String targetCode = target.get("code").getAsString();
                if(targetCode.equals("KD")) {
                    service = StarConstant.SERVICE_DONATE_MATCH;
                } else if (targetCode.equals("GT")) {
                    service = StarConstant.SERVICE_DONATE_GAMER;
                }else if (targetCode.equals("BLV"))
                {
                    service = StarConstant.SERVICE_DONATE_CASTER;
                }
                long receiverId = target.get("id").getAsLong();
                return new AoeBankAction(phoneNumber,service, receiverId);
            }
            else return null;
        } else if (matcher2.find()) {
            phoneNumber = matcher2.group(1);
            return new AoeBankAction(phoneNumber, StarConstant.SERVICE_STAR_RECHARGE);
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
