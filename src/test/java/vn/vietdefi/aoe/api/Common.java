package vn.vietdefi.aoe.api;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import vn.vietdefi.aoe.api.auth.AuthTestUtil;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class Common {
    public static long system_admin_id = 2;
    public static String system_admin_token = "0os6nq11fovc3cgyoab6x2fbk3zpl6tn";
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
            response = OkHttpUtil.postJson(deleteUrl, payload.toString(), AuthTestUtil.createHeader(system_admin_id, system_admin_token));
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
        JsonObject response = OkHttpUtil.get(url, AuthTestUtil.createHeader(userId, token));
        DebugLogger.info("{}", response);
        Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        return response;
    }
    public static JsonObject getProfileSuccess(String baseUrl, JsonObject data){
        long userid = data.get("id").getAsLong();
        String token = data.get("token").getAsString();
        return getProfileSuccess(baseUrl, userid, token);
    }
}
