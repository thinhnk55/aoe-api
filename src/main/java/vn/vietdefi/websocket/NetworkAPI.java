package vn.vietdefi.websocket;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.util.log.DebugLogger;

import java.util.Collection;
import java.util.List;

public class NetworkAPI {
    public static void send(long userId, JsonObject response) {
        String message = response.toString();
        send(userId, message);
    }

    public static void send(long userId, String message) {
        DebugLogger.info("send to {} {}", userId, message);
        List<WebsocketUser> users = WebsocketUserManager.instance().getUserById(userId);
        for(WebsocketUser user: users) {
            send(user, message);
        }
    }

    public static void send(WebsocketUser user, String message) {
        try {
            if (user != null && message != null && user.socket != null) {
                user.socket.writeTextMessage(message);
            }
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }
    public static void kickUser(long userId) {
        WebsocketUserManager.instance().kickUser(userId);
    }
}
