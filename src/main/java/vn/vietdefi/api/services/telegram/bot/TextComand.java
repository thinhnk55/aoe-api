package vn.vietdefi.api.services.telegram.bot;

import com.google.gson.JsonObject;
import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.api.services.telegram.util.TelegramConfig;
import vn.vietdefi.api.services.telegram.util.TelegramKeyboard;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;

import java.util.ArrayList;
import java.util.List;

public class TextComand {
    public static void process(VdefTelegramBot bot, Update update) {
        try {
            String command = update.message().text();
            long telegramId = update.message().from().id();
            if (command.startsWith("/start")) {
                showPhoneNumber(bot, telegramId);
//                startCommand(bot, update, command);
            } else if (command.startsWith(TelegramKeyboard.share_phone_number)) {
                sharePhoneCommand(bot, update);
            } else if (command.startsWith("/registerLogListener")) {
                registerLoglistener(bot, update, command);
            } else if (command.startsWith("/stopLogListener")) {
                stopLoglistener(bot, update, command);
            } else if (command.startsWith("/registerAdmin")) {
                registerAdmin(bot, update, command);
            } else if (command.startsWith("/stopAdmin")) {
                stopAdmin(bot, update, command);
            } else if (command.equals("/login")) {
                if (requestForPhoneNumber(bot, telegramId)) {
                    getLinkLogin(bot, update);
                }
            } else {
//                if(requestForPhoneNumber(bot, telegramId)) {
//                    sendSuggestLogin(bot, telegramId);
//                }
                if (!ApiServices.telegramService.isLinkedPhone(telegramId)) {
                    requestForPhoneNumber(bot, telegramId);
                } else {
                    bot.sendMessage(telegramId, "I will send OTP and update notification as need.");
                }
            }
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }

    public static void deleteMarkup(VdefTelegramBot bot, long telegramId) {
        SendMessage sendMessage = new SendMessage(telegramId, "Linked phone number successfully. I will send OTP and update notification as need.");
        Keyboard keyboard = new ReplyKeyboardMarkup(
                new KeyboardButton[]{
                        new KeyboardButton("Open Application")
                }
        );
        ((ReplyKeyboardMarkup) keyboard).resizeKeyboard(true);
        sendMessage.replyMarkup(keyboard);
        bot.bot.execute(sendMessage);
    }

    public static void sharePhoneCommand(VdefTelegramBot bot, Update update) {
        Contact contact = update.message().contact();
        long telegramId = update.message().from().id();
        if (contact != null) {
            String phone = contact.phoneNumber();
            JsonObject resonse = ApiServices.telegramService.updatePhoneNumber(telegramId, phone);
            if (BaseResponse.isSuccessFullMessage(resonse)) {
                deleteMarkup(bot, telegramId);
            } else {
                requestForPhoneNumber(bot, telegramId);
            }
        } else {
            requestForPhoneNumber(bot, telegramId);
        }
    }

    private static void registerAdmin(VdefTelegramBot bot, Update update, String command) {
        long from = update.message().from().id();
        if (bot.superAdmin == from) {
            try {
                long adminId = Long.parseLong(command.substring(15));
                bot.registerAdmin(adminId);
                String message = "Admin is registered successfully";
                bot.sendMessage(from, message);
                message = "Your request is accepted. You are admin now";
                bot.sendMessage(adminId, message);
            } catch (Exception e) {
                String message = "System Error";
                bot.sendMessage(from, message);
            }
        } else {
            String message = new StringBuilder("/registerAdmin_").append(from).toString();
            bot.sendMessage(bot.superAdmin, message);
            message = "Your request is sent to SUPER AMDIN. Please wait for process.";
            bot.sendMessage(from, message);
        }
    }


    private static void registerLoglistener(VdefTelegramBot bot, Update update, String command) {
        long from = update.message().from().id();
        if (bot.superAdmin == from) {
            try {
                long logListener = Long.parseLong(command.substring(21));
                if (bot.isLogListener(logListener)) {
                    String message = "You are log listener already";
                    bot.sendMessage(from, message);
                    return;
                }
                bot.registerLogListener(logListener);
                String message = "Log Listener is registered successfully";
                bot.sendMessage(from, message);
                message = "Your request is accepted. You are log listener now";
                bot.sendMessage(logListener, message);
            } catch (Exception e) {
                String message = "System Error";
                bot.sendMessage(from, message);
            }
        } else {
            String message = new StringBuilder("/registerLogListener_").append(from).toString();
            bot.sendMessage(bot.superAdmin, message);
            message = "Your request is sent to SUPER AMDIN. Please wait for process.";
            bot.sendMessage(from, message);
        }
    }

    private static void stopAdmin(VdefTelegramBot bot, Update update, String command) {
        long from = update.message().from().id();
        if (bot.superAdmin == from) {
            long admin = Long.parseLong(command.substring(11));
            bot.stopAdmin(admin);
        }
    }

    private static void stopLoglistener(VdefTelegramBot bot, Update update, String command) {
        long from = update.message().from().id();
        if (bot.superAdmin == from) {
            long logListener = Long.parseLong(command.substring(18));
            bot.stopLogListener(logListener);
        }
    }

    private static void showPhoneNumber(VdefTelegramBot bot, long telegramId) {
        SendMessage sendMessage = new SendMessage(telegramId, "Verify your telegram account !!!");
        Keyboard keyboard = new ReplyKeyboardMarkup(
                new KeyboardButton[]{
                        new KeyboardButton(TelegramKeyboard.share_phone_number).requestContact(true)
                }
        );
        sendMessage.replyMarkup(keyboard);
        ((ReplyKeyboardMarkup) keyboard).resizeKeyboard(true);
        sendMessage.replyMarkup(keyboard);
        bot.bot.execute(sendMessage);
    }

    public static void startCommand(VdefTelegramBot bot, Update update, String phoneNumber) {
        long telegramId = update.message().from().id();
        String firstName = update.message().chat().firstName();
        String lastName = update.message().chat().lastName();
        String fullName = new StringBuilder(firstName).append(" " + lastName).toString();
        JsonObject serviceResponse = ApiServices.telegramService.activeTelegramAccount(telegramId, fullName, phoneNumber);
        if (BaseResponse.isSuccessFullMessage(serviceResponse)) {
            SendMessage sendMessage = new SendMessage(telegramId, "Link successfully");
            ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
            sendMessage.replyMarkup(replyKeyboardRemove);
            bot.bot.execute(sendMessage);
            //requestForPhoneNumber(bot, telegramId);
        } else {
            if (serviceResponse.get("error").getAsInt() == 11) {
                String msg = new StringBuilder("Your telegram account has already been linked to account: ")
                        .append(ApiServices.telegramService.getUsername(telegramId)).toString();
                bot.sendMessage(telegramId, msg);
            } else {
                String message = new StringBuilder("You can control me by sending these commands:\n")
                        .append("/register - Create a new account for app\n")
                        .append("/login - Login to the app").toString();
                bot.sendMessage(telegramId, message);
            }
        }
    }

    //    private static void registerAccount(VdefTelegramBot bot, Update update, String command) {
//        long telegramId = update.message().from().id();
//        String firstName = update.message().chat().firstName();
//        String lastName = update.message().chat().lastName();
//        String fullName = new StringBuilder(firstName).append(" " + lastName).toString();
//        ApiServices.telegramService.registerByTelegram(telegramId, fullName);
//    }
    public static boolean requestForPhoneNumber(VdefTelegramBot bot, long telegramId) {
        boolean isExistPhone = ApiServices.telegramService.isExistPhoneNumber(telegramId);
        if (!isExistPhone) {
            SendMessage sendMessage = new SendMessage(telegramId, "Share your phone number !!!");
            Keyboard keyboard = new ReplyKeyboardMarkup(
                    new KeyboardButton[]{
                            new KeyboardButton(TelegramKeyboard.share_phone_number).requestContact(true)
                    }
            );
            sendMessage.replyMarkup(keyboard);
            ((ReplyKeyboardMarkup) keyboard).resizeKeyboard(true);
            sendMessage.replyMarkup(keyboard);
            bot.bot.execute(sendMessage);
        }
        return isExistPhone;
    }

    public static SendResponse sendNotification(VdefTelegramBot bot, long telegramId, String title, String link) {
        JsonObject config = TelegramConfig.instance().teleConfig;
        String message = "*{t}*\n";
        message = StringUtils.replace(message, "{t}", title);
        SendMessage response = new SendMessage(telegramId, message);

        if (link != null) {
            InlineKeyboardButton linkButton = new InlineKeyboardButton(config.get("user_login_button").getAsString());
            linkButton.url(link);
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(linkButton);
            response.replyMarkup(keyboardMarkup);
        }
        response.parseMode(ParseMode.Markdown);
        return bot.bot.execute(response);
    }

    public static SendResponse sendNotificationAdmin(VdefTelegramBot bot, long telegramId, String title, String link1, String link2) {
        JsonObject config = TelegramConfig.instance().teleConfig;
        String message = "*{t}*\n";
        message = StringUtils.replace(message, "{t}", title);
        SendMessage response = new SendMessage(telegramId, message);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(new InlineKeyboardButton(config.get("user_login_button").getAsString()).url(link1));
        rowInline.add(new InlineKeyboardButton(config.get("admin_login_button").getAsString()).url(link2));
        markupInline.addRow(rowInline.toArray(new InlineKeyboardButton[0]));
        response.replyMarkup(markupInline);
        response.parseMode(ParseMode.Markdown);
        return bot.bot.execute(response);
    }

    public static void sendSuggestLogin(VdefTelegramBot bot, long telegramId) {
        SendMessage sendMessage = new SendMessage(telegramId, "You can access by choosing \"login\" in the Menu or chat \"/login\" !!!");
        ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
        sendMessage.replyMarkup(replyKeyboardRemove);
        bot.bot.execute(sendMessage);
    }

    private static void getLinkLogin(VdefTelegramBot bot, Update update) {
        try {
            JsonObject config = TelegramConfig.instance().teleConfig;
            long telegramId = update.message().from().id();
            JsonObject user = ApiServices.telegramService.getUserByTelegramId(telegramId);
            int role = user.get("data").getAsJsonObject().get("role").getAsInt();
            long userid = user.get("data").getAsJsonObject().get("id").getAsLong();

            JsonObject otpJSON = ApiServices.otpService.generateRandomOTP(userid, 1);
            String otp = otpJSON.get("randomOtp").getAsString();
            String linkLoginUser = new StringBuilder(config.get("user_link").getAsString()).append("?telegramId=").append(telegramId).append("&otp").append(otp).toString();
            String linkLoginAdmin = new StringBuilder(config.get("admin_link").getAsString()).append("?telegramId=").append(telegramId).append("&otp").append(otp).toString();
            if (role == 0) {
                sendNotification(bot, telegramId, config.get("login_title").getAsString(), linkLoginUser);
            } else {
                sendNotificationAdmin(bot, telegramId, config.get("login_title").getAsString(), linkLoginUser, linkLoginAdmin);
            }
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }
}
