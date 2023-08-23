package vn.vietdefi.aoe.api.gamer;

import com.google.gson.JsonObject;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class GamerUtil {
    public static JsonObject getListGamer(String baseUrl){
        String url = new StringBuilder(baseUrl)
                .append("/gamer/list?page=").append(1).toString();
        JsonObject response = OkHttpUtil.get(url);
        DebugLogger.info("{}", response);
        return response;
    }
}
