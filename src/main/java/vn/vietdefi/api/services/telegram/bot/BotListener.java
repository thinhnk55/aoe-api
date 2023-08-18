package vn.vietdefi.api.services.telegram.bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.util.log.DebugLogger;

import java.util.List;

public class BotListener implements UpdatesListener {
    public IBotController controller;

    @Override
    public int process(List<Update> updates) {
        for(Update update: updates) {
            try {
                controller.process(update);
            }catch (Exception e){
                String stacktrace = ExceptionUtils.getStackTrace(e);
                DebugLogger.error(stacktrace);
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    public void setController(IBotController controller) {
        this.controller = controller;
    }
}