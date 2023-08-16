package vn.vietdefi.websocket;

import com.google.gson.JsonObject;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.vertx.core.http.ServerWebSocket;
import vn.vietdefi.util.log.DebugLogger;

import java.net.URLDecoder;

public class WebsocketServer {
    public static void handle(ServerWebSocket serverWebSocket) {
        String uri = serverWebSocket.uri();
        try {
            DebugLogger.info("WebsocketServer handle {}\n", uri);
            uri = URLDecoder.decode(uri, "UTF-8");
            QueryStringDecoder query = new QueryStringDecoder(uri);
            long userId = Long.parseLong(query.parameters().get("id").get(0));
            String token = query.parameters().get("token").get(0);
            WebsocketUser websocketUser = WebsocketUserManager.instance().login(serverWebSocket, userId, token);
            if (websocketUser == null) {
                JsonObject response = WebsocketResponse.create(WebsocketID.LOGIN, 2);
                serverWebSocket.writeTextMessage(response.toString());
                serverWebSocket.close();
            } else {
                JsonObject response = WebsocketResponse.create(WebsocketID.LOGIN, 0);
                serverWebSocket.writeTextMessage(response.toString());
            }
        } catch (Exception e) {
            JsonObject response = WebsocketResponse.create(WebsocketID.LOGIN, 1);
            serverWebSocket.writeTextMessage(response.toString());
            serverWebSocket.close();
            e.printStackTrace();
        }
    }
}
