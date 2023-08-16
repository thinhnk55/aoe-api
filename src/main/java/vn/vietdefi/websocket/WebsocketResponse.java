package vn.vietdefi.websocket;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class WebsocketResponse {
    public static JsonObject create(int requestId, int error) {
        JsonObject json = new JsonObject();
        json.addProperty("id", requestId);
        json.addProperty("e", error);
        return json;
    }

    public static JsonObject create(int requestId) {
        JsonObject json = new JsonObject();
        json.addProperty("id", requestId);
        json.addProperty("e", 0);
        return json;
    }

    public static JsonObject create(int requestId, int error, JsonElement data) {
        JsonObject json = new JsonObject();
        json.addProperty("id", requestId);
        json.addProperty("e", error);
        json.add("d", data);
        return json;
    }

    public static JsonObject create(int requestId, JsonElement data) {
        JsonObject json = new JsonObject();
        json.addProperty("id", requestId);
        json.addProperty("e", 0);
        json.add("d", data);
        return json;
    }

    public static boolean isSuccess(JsonObject response) {
        if (response.has("e") && response.get("e").getAsInt() == 0) {
            return true;
        }
        return false;
    }

    public static JsonObject updateError(JsonObject json, int error) {
        json.addProperty("e", error);
        return json;
    }


    public static JsonObject updateData(JsonObject json, JsonElement data) {
        json.add("d", data);
        return json;
    }

    public static JsonObject getData(JsonObject response) {
        JsonObject data = null;
        if (response.has("d")) {
            data = response.getAsJsonObject("d");
        }
        return data;
    }
}
