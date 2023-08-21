package vn.vietdefi.aoe.api.auth;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class AuthTestUtil {
    public static Map<String, String> createHeader(long userid, String token){
        Map<String, String> headers = new HashMap<>();
        headers.put("userid", String.valueOf(userid));
        headers.put("token", token);
        return headers;
    }
    public static Map<String, String> createHeader(JsonObject data){
        long userid = data.get("id").getAsLong();
        String token = data.get("token").getAsString();
        return createHeader(userid, token);
    }
}
