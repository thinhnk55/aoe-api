package vn.vietdefi.api.services.telegram;

import com.google.gson.JsonObject;
import com.pengrad.telegrambot.response.SendResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.api.services.telegram.bot.VdefTelegramBot;
import vn.vietdefi.api.services.telegram.util.TelegramConfig;
import vn.vietdefi.api.services.telegram.util.TelegramMessage;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;
import vn.vietdefi.util.string.StringUtil;
import vn.vietdefi.util.thread.ThreadPoolWorker;

public class TelegramService implements ITelegramService {
    public JsonObject requestLinkAccount(JsonObject json){
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long user_id = json.get("userid").getAsLong();
            String query = "SELECT username FROM user WHERE id = ?";
            String username = bridge.queryString(query, user_id);
            JsonObject config = VdefTelegramBot.instance().config;
            String url = config.get("url").getAsString();
            String activeCode = StringUtil.generateRandomStringNumberCharacter(6);
            url = new StringBuilder(url).append("?start=").append(activeCode).toString();
            json = new JsonObject();
            json.addProperty("link", url);
            return BaseResponse.createFullMessageResponse(0, "success", json);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject activeTelegramAccount(long telegram_id, String fullName, String phoneNumber) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            if(isAccountLinked(telegram_id)){
                return BaseResponse.createFullMessageResponse(11, "telegram_account_linked_before");
            }
            Long user_id = bridge.queryLong("SELECT id FROM user WHERE username = ?", phoneNumber);
            if (user_id != null) {
                String insert = "INSERT INTO telegram_user(user_id ,username, telegram_id, phone_number, active_code, telegram_info) VALUE(?,?,?,?,?,?)";
                bridge.update(insert, user_id, phoneNumber, telegram_id, phoneNumber, "", fullName);
                return BaseResponse.createFullMessageResponse(0, "success");
            } else {
                return BaseResponse.createFullMessageResponse(10, "invalid_telegram");
            }
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject sendMessage(final long telegramdId, final String message) {
        ThreadPoolWorker.instance().executor.execute(() -> {
            VdefTelegramBot.instance().sendMessage(telegramdId, message);
        });
        return BaseResponse.createFullMessageResponse(0, "success");
    }

    @Override
    public JsonObject sendMessageToAdmin(final String message) {
        ThreadPoolWorker.instance().executor.execute(() -> {
            VdefTelegramBot.instance().sendAdminMessage(message);
        });
        return BaseResponse.createFullMessageResponse(0, "success");
    }

    @Override
    public JsonObject sendLogMessage(final String message) {
        ThreadPoolWorker.instance().executor.execute(() -> {
            VdefTelegramBot.instance().sendLogMessage(message);
        });
        return BaseResponse.createFullMessageResponse(0, "success");
    }

    @Override
    public JsonObject updatePhoneNumber(long telegram_id, String phone) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            if(phone.startsWith("+")){
                phone = phone.substring(1);
            }
            String query = "UPDATE telegram_user SET phone_number = ? WHERE telegram_id = ?";
            int row = bridge.update(query, phone, telegram_id);
            if(row == 0){
                return BaseResponse.createFullMessageResponse(2, "cannot_link_phone_number");

            }
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public void sendNotificationService(long user_id, JsonObject data) {
        try {
            long telegram_id = gettelegram_id(user_id);
            JsonObject content = data.get("content").getAsJsonObject();
            String title = "";
            String mainContent = "";
            String link = "";
            if(content.has("title")){
                title = content.get("title").getAsString();
            }
            if(content.has("content")){
                mainContent = content.get("content").getAsString();
            }
            if(content.has("link")){
                link = content.get("link").getAsString();
            }
            VdefTelegramBot.instance().sendNotification(telegram_id, title,mainContent,link);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }
    public JsonObject sendTelegramOTP(long user_id) {
        try {
            long telegram_id = ApiServices.telegramService.getTelegramId(user_id);
            if (telegram_id == -1) {
                return BaseResponse.createFullMessageResponse(10, "link_telegram_first");
            } else {
                JsonObject json = ApiServices.otpService.generateRandomOTP(user_id, 1);
                if (json == null) {
                    return BaseResponse.createFullMessageResponse(1, "system_error");
                } else {
                    int otp = json.get("randomOtp").getAsInt();
                    VdefTelegramBot.instance().sendNotification(telegram_id,"OTP CODE","Do not share for anyone\n"+otp,null);
                    return BaseResponse.createFullMessageResponse(0, "success", json);
                }
            }
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public long getTelegramId(long userid) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            Long telegramId = bridge.queryLong("SELECT telegram_id FROM telegram_user WHERE user_id = ?", userid);
            return telegramId;
        }catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return -1;
        }
    }

    public long gettelegram_id(long user_id){
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            Long telegram_id = bridge.queryLong("SELECT telegram_id FROM telegram_user WHERE user_id = ?", user_id);
            return telegram_id;
        }catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return -1;
        }
    }

    @Override
    public JsonObject checkLinkTelegram(long user_id) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT telegram_info FROM telegram_user WHERE user_id = ?";
            JsonObject data = bridge.queryOne(query, user_id);
            try{
                data.get("telegram_info").getAsString();
                return BaseResponse.createFullMessageResponse(0,"linked",data);
            }catch (Exception e){
                e.printStackTrace();
                return BaseResponse.createFullMessageResponse(10,"not_link");
            }
        }catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1,"system_error");
        }
    }
    public JsonObject deleteLink(long user_id) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "DELETE FROM telegram_user WHERE user_id = ?";
            bridge.update(query, user_id);
            return BaseResponse.createFullMessageResponse(0,"success");
        }catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1,"system_error");
        }
    }
    public String getTelegramInfo(long user_id){
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT telegram_info FROM telegram_user WHERE user_id = ?";
            JsonObject data = bridge.queryOne(query, user_id);
            return data.get("telegram_info").getAsString();
        }catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return null;
        }
    }
    public String getUsername(long telegram_id){
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT username FROM telegram_user WHERE telegram_id = ?";
            JsonObject json = bridge.queryOne(query, telegram_id);
            String username = json.get("username").getAsString();
            return username;
        }catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return "";
        }
    }

    boolean isAccountLinked(long telegram_id){
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            return bridge.queryExist("SELECT telegram_info FROM telegram_user WHERE telegram_id = ?",telegram_id);
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return false;
        }
    }
    public boolean isLinkedPhone(long  telegram_id){
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject check = bridge.queryOne("SELECT phone_number FROM telegram_user WHERE telegram_id = ?",telegram_id);
            return !check.get("phone_number").isJsonNull();
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return false;
        }
    }
    @Override
    public boolean checkAccountTelegram(String phoneNumber) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            return bridge.queryExist("SELECT * FROM user WHERE username = ?", phoneNumber);
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return false;
        }
    }


    //Login by telegram
    @Override
    public JsonObject getLinkTelegramBot() {
        try {
            JsonObject teleConfig = TelegramConfig.instance().teleBot;
            JsonObject data = new JsonObject();
            data.addProperty("link", teleConfig.get("url").getAsString());
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }
        catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    @Override
    public JsonObject sendTelegramOTPByPhoneNumber(String phoneNumber) {
        try{
            if(!AoeServices.userService.isExistPhone(phoneNumber)){
                return BaseResponse.createFullMessageResponse(14, "phone_not_found");
            }
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM telegram_user WHERE username=?";
            JsonObject data = bridge.queryOne(query, phoneNumber);
            if(data == null) {
                return BaseResponse.createFullMessageResponse(15, "account_not_linked");
            }
            else {
                long user_id = data.get("user_id").getAsLong();
                long telegram_id = data.get("telegram_id").getAsLong();
                JsonObject otpJSON = ApiServices.otpService.generateRandomOTP(user_id, 1);
                String otp = otpJSON.get("randomOtp").getAsString();
                SendResponse message = VdefTelegramBot.instance().sendMessage(telegram_id, "This is my otp code. Invalid 60s: " + otp);
                if(message.isOk()) {
                    return BaseResponse.createFullMessageResponse(0, "success");
                }
                else {
                    return BaseResponse.createFullMessageResponse(2, message.description());
                }
            }
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    @Override
    public JsonObject getTelegramInfoByPhoneNumber(String phone_number) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM telegram_user WHERE phone_number = ?";
            return bridge.queryOne(query, phone_number);
        }
        catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return null;
        }
    }
    @Override
    public JsonObject getUserByTelegramId(long telegram_id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM user WHERE username=?";
            JsonObject res = bridge.queryOne(query, telegram_id);
            return BaseResponse.createFullMessageResponse(0, "success", res);
        }
        catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return null;
        }
    }

    /**
     Check user register in telegram_user
     */
    public boolean isExistTelegramUser(long telegram_id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM telegram_user WHERE telegram_id=?";
            boolean isUser = bridge.queryExist(query, telegram_id);
            return isUser;
        }
        catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return false;
        }
    }

    /**
     Check user register phone in telegram_user
     */
    public boolean isExistPhoneNumber(long telegram_id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT phone_number FROM telegram_user WHERE telegram_id = ?";
            String phoneNumber = bridge.queryString(query, telegram_id);
            return phoneNumber != null;
        }
        catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return false;
        }
    }
    @Override
    public void sendWarning(long user_id, long accountNumber, String accountName) {
        try {
            long telegram_id = ApiServices.telegramService.getTelegramId(user_id);
            String message = StringUtils.replace(TelegramMessage.stopActiveWarning,"{o}", String.valueOf(accountName));
            message = StringUtils.replace(message,"{n}", String.valueOf(accountNumber));
            VdefTelegramBot.instance().sendMessage(telegram_id, message);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }

    @Override
    public void sendStateMatch(long matchId, String team1, String team2) {
        try {

        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }
}
