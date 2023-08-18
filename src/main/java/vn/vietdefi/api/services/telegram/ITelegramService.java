package vn.vietdefi.api.services.telegram;

import com.google.gson.JsonObject;

public interface ITelegramService {
    JsonObject requestLinkAccount(JsonObject json);
    JsonObject activeTelegramAccount(long telegramId,String fullName, String phoneNumber);
    JsonObject sendMessage(final long telegramdId, final String message);
    JsonObject sendMessageToAdmin(final String message);
    JsonObject sendLogMessage(final String message);
    JsonObject updatePhoneNumber(long telegramId, String phone);
    void sendNotificationService(long userId, JsonObject data);
    JsonObject sendTelegramOTP(long userId);
    long getTelegramId(long userid);
    JsonObject checkLinkTelegram(long userId);
    JsonObject deleteLink(long userId);

    String getTelegramInfo(long userId);
    String getUsername(long telegramId);
    boolean isLinkedPhone(long  telegramId);
    boolean checkAccountTelegram(String phoneNumber);
    /*login by telegram*/
    JsonObject getLinkTelegramBot();
    JsonObject sendTelegramOTPByPhoneNumber (String phoneNumber);
    JsonObject getTelegramInfoByPhoneNumber(String phoneNumber  );
    JsonObject getUserByTelegramId(long telegramId);
    boolean isExistPhoneNumber(long telegramId);
    /*Send notification*/
    void sendWarning (long userId, long accountNumber, String accountName);
    void sendStateMatch(long matchId, String team1, String team2);
}
