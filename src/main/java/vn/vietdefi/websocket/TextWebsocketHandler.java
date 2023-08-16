package vn.vietdefi.websocket;

import com.google.gson.JsonObject;
import io.vertx.core.Handler;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class TextWebsocketHandler implements Handler<String> {
    WebsocketUser websocketUser;

    public TextWebsocketHandler(WebsocketUser websocketUser) {
        this.websocketUser = websocketUser;
    }

    @Override
    public void handle(String event) {
        try {
            if(event == null){
                DebugLogger.error("TextWebsocketHandler handle error: event null userId = {}", websocketUser.userId);
                return;
            }
            JsonObject request = GsonUtil.toJsonObject(event);
            if(request == null){
                DebugLogger.error("TextWebsocketHandler handle error: event {} userId = {}", event, websocketUser.userId);
                return;
            }
            int requestId = request.get("id").getAsInt();
            switch (requestId){
                case WebsocketID.PING_PONG:
                    pong();
                    break;
                default:
                    defaultHandle(requestId, request);
                    break;
            }
        } catch (Exception e) {
            DebugLogger.error("TextWebsocketHandler handle error: {}, userId = {}", event, websocketUser.userId);
            e.printStackTrace();
        }
    }

    private void defaultHandle(int requestId, JsonObject request) {
        long userId = websocketUser.userId;
        HandlerManager.instance().localHandle(userId, requestId, request);
    }

    private void pong() {
        JsonObject response = WebsocketResponse.create(WebsocketID.PING_PONG, 0);
        websocketUser.socket.writeTextMessage(response.toString());
    }
}
