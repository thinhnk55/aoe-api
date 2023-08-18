package vn.vietdefi.api.services.telegram.util;

import com.google.gson.JsonObject;
import vn.vietdefi.util.file.FileUtil;

public class TelegramConfig {
    private static TelegramConfig ins = null;
    private TelegramConfig () {

    }
    public static TelegramConfig instance() {
        if (ins == null) {
            ins = new TelegramConfig();
        }
        return ins;
    }

    public JsonObject teleBot = FileUtil.getJsonObject("config/telegram/bot.json");
    public JsonObject teleConfig = FileUtil.getJsonObject("config/telegram/clientInfo.json");
}
