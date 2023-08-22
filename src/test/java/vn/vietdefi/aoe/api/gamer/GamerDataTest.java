package vn.vietdefi.aoe.api.gamer;

import com.google.gson.JsonObject;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class GamerDataTest {
    public static JsonObject deleteGamer(String baseUrl, String username){
        String loginUrl = new StringBuilder(baseUrl)
                .append("/auth/login/gamer").toString();
        String deleteUrl = new StringBuilder(baseUrl)
                .append("/auth/delete_user").toString();
        String deleteGamerUrl = new StringBuilder(baseUrl)
                .append("/gamer/delete").toString();
        JsonObject payload = new JsonObject();
        payload.addProperty("username", username);
        JsonObject response = OkHttpUtil.postJson(loginUrl, payload.toString(),Common.createHeaderSystemAdmin());
        if(BaseResponse.isSuccessFullMessage(response)){
            long id = response.getAsJsonObject("data").get("id").getAsLong();
            payload = new JsonObject();
            payload.addProperty("user_id", id);
            response = OkHttpUtil.postJson(deleteUrl, payload.toString(), Common.createHeaderSystemAdmin());
            if (BaseResponse.isSuccessFullMessage(response)){
                response = OkHttpUtil.postJson(deleteGamerUrl, payload.toString(), Common.createHeaderSystemAdmin());
                DebugLogger.info("{}", response);
                return response;
            }else {
                return response;
            }
        }else{
            return response;
        }
    }


}
