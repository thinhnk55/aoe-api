package vn.vietdefi.util.network;

import io.vertx.core.http.HttpServerRequest;

public class IPUtil {
    public static String readIPFromRequest(HttpServerRequest request) {
        String ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = request.remoteAddress().hostAddress();
        }
        return ip;
    }
}
