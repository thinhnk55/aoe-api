package vn.vietdefi.api.services.telegram.bot;


import com.pengrad.telegrambot.model.Update;

public interface IBotController {
    public void process(Update updates);
}