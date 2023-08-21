package vn.vietdefi.aoe.api;

import java.util.HashMap;
import java.util.Map;

public class AuthTestUtil {
    public static Map<String, String> createHeader(long userid, String token){
        Map<String, String> headers = new HashMap<>();
        headers.put("userid", String.valueOf(userid));
        headers.put("token", token);
        return headers;
    }
}
