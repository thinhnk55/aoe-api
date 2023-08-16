package vn.vietdefi.websocket;

import com.google.gson.JsonObject;
import io.vertx.core.http.ServerWebSocket;

import java.util.UUID;

public class WebsocketUser {
    public String sessionId;
    public long userId;
    public String channel;
    public int role;
    public ServerWebSocket socket;
    public WebsocketUser() {
        sessionId = UUID.randomUUID().toString();
    }

    public WebsocketUser(JsonObject json) {
        this.sessionId = json.get("s").getAsString();
        this.userId = json.get("u").getAsLong();
        this.channel = json.get("c").getAsString();
        this.role = json.get("r").getAsInt();
    }


    public JsonObject toShortJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("s", sessionId);
        json.addProperty("u", userId);
        json.addProperty("c", channel);
        json.addProperty("r", role);
        return json;
    }
}