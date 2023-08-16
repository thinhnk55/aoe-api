package vn.vietdefi.websocket;

import io.vertx.core.Handler;

public class CloseWebSocketHandler implements Handler<Void> {
    WebsocketUser websocketUser;

    public CloseWebSocketHandler(WebsocketUser websocketUser) {
        this.websocketUser = websocketUser;
    }

    @Override
    public void handle(Void event) {
        WebsocketUserManager.instance().logout(websocketUser);
    }
}
