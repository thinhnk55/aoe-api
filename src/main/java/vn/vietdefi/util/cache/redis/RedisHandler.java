package vn.vietdefi.util.cache.redis;

public interface RedisHandler {
    void onMessage(String channel, String message);
}
