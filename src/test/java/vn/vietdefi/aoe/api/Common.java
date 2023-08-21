package vn.vietdefi.aoe.api;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

import java.util.HashMap;
import java.util.Map;

public class Common {
    public static long system_admin_id = 106;
    public static String system_admin_token = "zlmnyk66fi0lhgkr7ol4sqld27xsg1ip";

    public static long support_id = 5;
    public static String support_token = "jv17348v1vxy8k11p87l1tjupd2a4l03";

    public static long admin_id = 4;
    public static String admin_token = "0wmv1eiy8yjvp1tc9mjtovj623as7jmu";

    public static long super_admin_id = 1;
    public static String super_admin_token = "2gbpnlvqtidiifohxnqb1thw1un969uq";

    public static JsonObject deleleUser(String baseUrl, String username, String password){
        String loginUrl = new StringBuilder(baseUrl)
                .append("/auth/login").toString();
        String deleteUrl = new StringBuilder(baseUrl)
                .append("/auth/delete_user").toString();
        JsonObject payload = new JsonObject();
        payload.addProperty("username", username);
        payload.addProperty("password", password);
        JsonObject response = OkHttpUtil.postJson(loginUrl, payload.toString());
        if(BaseResponse.isSuccessFullMessage(response)){
            long id = response.getAsJsonObject("data").get("id").getAsLong();
            payload = new JsonObject();
            payload.addProperty("user_id", id);
            response = OkHttpUtil.postJson(deleteUrl, payload.toString(), createHeaderSystemAdmin());
            DebugLogger.info("{}", response);
            return response;
        }else{
            return response;
        }
    }

    public static JsonObject registerUserSuccess(String baseUrl, String username, String password){
        String registerUrl = new StringBuilder(baseUrl)
                .append("/auth/register").toString();
        JsonObject payload = new JsonObject();
        payload.addProperty("username", username);
        payload.addProperty("password", password);
        JsonObject response = OkHttpUtil.postJson(registerUrl, payload.toString());
        Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        return response;
    }
    public static JsonObject loginUserSuccess(String baseUrl, String username, String password){
        String loginUrl = new StringBuilder(baseUrl)
                .append("/auth/login").toString();
        JsonObject payload = new JsonObject();
        payload.addProperty("username", username);
        payload.addProperty("password", password);
        JsonObject response = OkHttpUtil.postJson(loginUrl, payload.toString());
        Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        return response;
    }
    public static void loginUserFailed(String baseUrl, String username, String password){
        String loginUrl = new StringBuilder(baseUrl)
                .append("/auth/login").toString();
        JsonObject payload = new JsonObject();
        payload.addProperty("username", username);
        payload.addProperty("password", password);
        JsonObject response = OkHttpUtil.postJson(loginUrl, payload.toString());
        Assertions.assertTrue(!BaseResponse.isSuccessFullMessage(response));
    }

    public static JsonObject getProfileSuccess(String baseUrl, long userId, String token){
        String url = new StringBuilder(baseUrl)
                .append("/profile/get").toString();
        JsonObject response = OkHttpUtil.get(url, createHeader(userId, token));
        DebugLogger.info("{}", response);
        Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        return response;
    }
    public static JsonObject getProfileSuccess(String baseUrl, JsonObject data){
        long userid = data.get("id").getAsLong();
        String token = data.get("token").getAsString();
        return getProfileSuccess(baseUrl, userid, token);
    }

    public static Map<String, String> createHeaderSupport(){
        return createHeader(support_id, support_token);
    }
    public static Map<String, String> createHeaderAdmin(){
        return createHeader(admin_id, admin_token);
    }
    public static Map<String, String> createHeaderSupperAdmin(){
        return createHeader(super_admin_id, super_admin_token);
    }
    public static Map<String, String> createHeaderSystemAdmin(){
        return createHeader(system_admin_id, system_admin_token);
    }

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
