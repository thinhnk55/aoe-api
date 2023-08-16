package vn.vietdefi.util.firebase;

import com.google.gson.JsonObject;
import vn.vietdefi.util.file.FileUtil;
import vn.vietdefi.util.network.OkHttpUtil;

import java.util.HashMap;
import java.util.Map;

public class SendFCM {
    public static JsonObject config = FileUtil.getJsonObject("config/fcm/server.json");
    static final String url = config.get("url").getAsString();
    static final String serverKey = config.get("server-key").getAsString();
    public static JsonObject sendToDevice(JsonObject noti) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "key=" + serverKey);
        headers.put("Content-Type", "applicaton/json");
        JsonObject response = OkHttpUtil.postJson(url, noti.toString(), headers);
        return response;
    }
}
