package vn.vietdefi.websocket;

import com.google.gson.JsonObject;
import vn.vietdefi.websocket.event.IEventListener;

public interface WebsocketHandler extends IEventListener {
    void handle(long userId, int requestId, JsonObject request);
}
