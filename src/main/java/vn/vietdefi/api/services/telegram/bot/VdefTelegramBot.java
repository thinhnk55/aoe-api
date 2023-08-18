package vn.vietdefi.api.services.telegram.bot;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.api.services.telegram.util.TelegramConfig;
import vn.vietdefi.util.file.FileUtil;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

import java.util.LinkedList;
import java.util.List;

public class VdefTelegramBot implements IBotController {
    private static VdefTelegramBot ins;
    private VdefTelegramBot() {

    }
    public static VdefTelegramBot instance() {
        if (ins == null) {
            ins = new VdefTelegramBot();
        }
        return ins;
    }
    public JsonObject config;
    TelegramBot bot;
    BotListener listener;


    public void run() {
        JsonObject config = TelegramConfig.instance().teleBot;
        VdefTelegramBot.instance().start(config);
    }
    private void start(JsonObject config){
        this.config = config;
        String token = config.get("token").getAsString();
        this.bot = new TelegramBot(token);
        this.listener = new BotListener();
        this.bot.setUpdatesListener(listener);
        listener.setController(this);
        this.loggerOn = config.get("logger_on").getAsBoolean();
        JsonArray defaultLogListeners = config.getAsJsonArray("log_listeners");
        for(int i = 0; i < defaultLogListeners.size(); i++){
            long  logListenerTelegramId = defaultLogListeners.get(i).getAsLong();
            registerLogListener(logListenerTelegramId);
        }
        this.superAdmin = config.get("super_admin").getAsLong();
        JsonArray admins = config.getAsJsonArray("admins");
        for(int i = 0; i < admins.size(); i++){
            long  admin = admins.get(i).getAsLong();
            registerAdmin(admin);
        }
    }
    @Override
    public void process(Update update) {
        try {
            if(update.message() != null && update.message().text() != null){
                TextComand.process(this, update);
            }
            if(update.message().contact() != null){
                ContactCommand.process(this, update);
            }
        }catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }

    public SendResponse sendMessage(long telegramId, String message){
        SendMessage response = new SendMessage(telegramId, message);
        return bot.execute(response);
    }
    public SendResponse sendNotification(long telegramId, String title,String content,String link){
        String message = "*{t}*\n\n_{c}_\n";
        message = StringUtils.replace(message,"{t}",title);
        message = StringUtils.replace(message,"{c}",content);

        SendMessage response = new SendMessage(telegramId, message);

        if(link!=null){
            InlineKeyboardButton linkButton = new InlineKeyboardButton("Click for navigation");
            linkButton.url(link);
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(linkButton);
            response.replyMarkup(keyboardMarkup);
        }
        response.parseMode(ParseMode.Markdown);
        return bot.execute(response);
    }

    long superAdmin;
    public List<Long> admins = new LinkedList<>();
    public void registerAdmin(long telegramId){
        admins.add(telegramId);
        updateConfig();
    }

    public void stopAdmin(long telegramId){
        admins.remove(telegramId);
        updateConfig();
    }

    public void sendAdminMessage(String message){
        if(!loggerOn){
            return;
        }
        if(logListeners.size() > 0) {
            for(Long telegramId: logListeners)
                sendMessage(telegramId, message);
        }
    }

    public List<Long> logListeners = new LinkedList<>();
    public boolean loggerOn = false;
    public void registerLogListener(long telegramId){
        logListeners.add(telegramId);
        updateConfig();
    }
    public void stopLogListener(long telegramId){
        logListeners.remove(telegramId);
        updateConfig();
    }

    public void sendLogMessage(String message){
        if(!loggerOn){
            return;
        }
        if(logListeners.size() > 0) {
            for(Long telegramId: logListeners)
            sendMessage(telegramId, message);
        }
    }
    public void updateConfig(){
        JsonArray adminsArr = new JsonArray();
        for(Long admin: admins){
            adminsArr.add(admin);
        }
        this.config.add("admins", adminsArr);
        JsonArray logListennerArr = new JsonArray();
        for(Long admin: logListeners){
            logListennerArr.add(admin);
        }
        this.config.add("log_listeners", logListennerArr);
        FileUtil.writeStringToFile("config/telegram/bot.json",
                GsonUtil.toBeautifulJsonStringGson(config));
    }

    public boolean isAdmin(long adminId) {
        return admins.contains(adminId);
    }
    public boolean isLogListener(long adminId) {
        return admins.contains(adminId);
    }
}
