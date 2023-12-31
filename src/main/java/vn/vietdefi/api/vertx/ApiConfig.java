package vn.vietdefi.api.vertx;

import com.google.gson.JsonObject;
import vn.vietdefi.util.file.FileUtil;

public class ApiConfig {
    private static ApiConfig ins = null;
    public static ApiConfig instance() {
        if (ins == null) {
            ins = new ApiConfig();
        }
        return ins;
    }
    private ApiConfig(){

    }

    public void init(String configFile) {
        JsonObject config = FileUtil.getJsonObject(configFile);
        this.name = config.get("name").getAsString();
        this.url_prefix = config.get("url_prefix").getAsString();
        this.http_port= config.get("http_port").getAsInt();
        this.websocket_port = config.get("websocket_port").getAsInt();
    }

    public String name;
    public int http_port;
    public int websocket_port;
    public String url_prefix;

    public String getPath(String path) {
        String fullPath = new StringBuilder(url_prefix).append(path).toString();
        return fullPath;
    }
}
