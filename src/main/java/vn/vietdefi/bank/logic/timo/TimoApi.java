package vn.vietdefi.bank.logic.timo;

import com.google.gson.JsonObject;
import okhttp3.Response;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

import java.util.HashMap;
import java.util.Map;

public class TimoApi {
    public static JsonObject login(JsonObject auth) {
        try {
            String username = auth.get("username").getAsString();
            String password = auth.get("password").getAsString();
            JsonObject body = new JsonObject();
            body.addProperty("username", username);
            body.addProperty("password", password);
            Map<String, String> headers = new HashMap<>();
            headers.put("x-timo-devicereg", TimoConfig.xTimoDevice);
            try (Response response = OkHttpUtil.postFullResponse(TimoConfig.URL_LOGIN, body.toString(), headers)) {
                if (response.code() == 200) {
                    String responseBody = response.body().string();
                    JsonObject res = GsonUtil.toJsonObject(responseBody);
                    if (res.get("code").getAsInt() == 6001) {
                        JsonObject data = res.get("data").getAsJsonObject();
                        return BaseResponse.createFullMessageResponse(6001, "not_commit", data);
                    }
                    if (res.get("code").getAsInt() == 200) {
                        JsonObject data = res.get("data").getAsJsonObject();
                        return BaseResponse.createFullMessageResponse(200, "login_success", data);
                    }
                    if (res.get("code").getAsInt() == 401) {
                        return BaseResponse.createFullMessageResponse(401, "account_invalid");
                    }
                }
                return BaseResponse.createFullMessageResponse(1, "system_error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
