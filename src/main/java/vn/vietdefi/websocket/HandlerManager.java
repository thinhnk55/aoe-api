package vn.vietdefi.websocket;

import com.google.gson.JsonObject;
import vn.vietdefi.util.log.DebugLogger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerManager {
    public static final int SEPARATOR = 1000;
    private static HandlerManager ins = null;

    public static HandlerManager instance() {
        if (ins == null) {
            ins = new HandlerManager();
        }
        return ins;
    }

    Map<Integer, WebsocketHandler> handlers = new ConcurrentHashMap<>();

    public int makeKey(int id) {
        return ((id / SEPARATOR) * SEPARATOR);
    }

    public void addHandler(int key, WebsocketHandler websocketHandler) {
        try {
            handlers.put(key, websocketHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public WebsocketHandler findHandler(int id) {
        int key = makeKey(id);
        return handlers.get(key);
    }

    public void localHandle(long userId, int requestId, JsonObject request) {
        WebsocketHandler handler = findHandler(requestId);
        if (handler != null) {
            handler.handle(userId,  requestId, request);
//            DebugLogger.debug(request.toString());
        } else {
            DebugLogger.error("WebsocketHandler not found {} content: {}", request);
        }
    }
}
