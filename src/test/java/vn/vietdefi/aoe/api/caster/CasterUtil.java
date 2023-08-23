package vn.vietdefi.aoe.api.caster;

import com.google.gson.JsonObject;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class CasterUtil {
    public static JsonObject getListCaster(String baseUrl){
        String url = new StringBuilder(baseUrl)
                .append("/caster/list?page=1").toString();
        JsonObject response = OkHttpUtil.get(url);
        DebugLogger.info("{}", response);
        return response;
    }
}
