package vn.vietdefi.api.services.telegram.bot;

import com.google.gson.JsonObject;
import com.pengrad.telegrambot.model.Update;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;

public class ContactCommand {
    public static void process(VdefTelegramBot bot, Update update) {
        try {
            long telegramId = update.message().from().id();
            String phoneNumber = "0" + update.message().contact().phoneNumber().substring(3);
            //check link telegram account
            if (ApiServices.telegramService.checkAccountTelegram(phoneNumber)) {
                TextComand.startCommand(bot, update, phoneNumber);
            } else {
                bot.sendMessage(telegramId, "Invalid telegram account!");
                return;
            }
            if (ApiServices.telegramService.isLinkedPhone(telegramId)) {
                return;
            } else {
                JsonObject updatePhoneNumber = ApiServices.telegramService.updatePhoneNumber(telegramId, phoneNumber);
                if(BaseResponse.isSuccessFullMessage(updatePhoneNumber)) {
                    bot.sendMessage(telegramId, "Link number phone to account success!!");
                    // Link number phone to account success
                    TextComand.sendSuggestLogin(bot, telegramId);
                }
                else {
                    bot.sendMessage(telegramId, "Link number phone to account fail!!");
                    TextComand.requestForPhoneNumber(bot, telegramId);
                }
            }
        } catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }


}
