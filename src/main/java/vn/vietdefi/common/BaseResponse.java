package vn.vietdefi.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class BaseResponse {
    public static JsonObject createFullMessageResponse(int error, String message, JsonElement data){
        JsonObject json = new JsonObject();
        json.addProperty("error", error);
        json.addProperty("message", message);
        json.add("data", data);
        return json;
    }
    public static JsonObject createFullMessageResponse(int error, String message){
        JsonObject json = new JsonObject();
        json.addProperty("error", error);
        json.addProperty("message", message);
        return json;
    }
    public static JsonObject createShortMessageResponse(int error, JsonElement data){
        JsonObject json = new JsonObject();
        json.addProperty("e", error);
        json.add("d", data);
        return json;
    }
    public static JsonObject createShortMessageResponse(int error){
        JsonObject json = new JsonObject();
        json.addProperty("e", error);
        return json;
    }

    public static boolean isSuccessShortMessage(JsonObject response) {
        return response.get("e").getAsInt() == 0;
    }

    public static boolean isSuccessFullMessage(JsonObject response) {
        return response.get("error").getAsInt() == 0;
    }
}