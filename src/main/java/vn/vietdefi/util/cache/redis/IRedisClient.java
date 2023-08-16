package vn.vietdefi.util.cache.redis;

import java.util.List;

public interface IRedisClient {
    long llen(String key);
    String rpop(String key);
    String lpop(String key);
    void rpush(String key, String value);
    void lpush(String key, String value);
    String set(String key, String value);
    void del(String key);
    void setAsync(String key, String value);
    void setExpire(String key, String value, int expire);
    String get(String key);
    void publish(String channel, String message);
    List<String> getList(String key, long from, long to);
    void insertList(String key, String data, int expire, int maxSize);
    long generateId(String key);
    Object get(String key, Class clazz);
    String set(String key, Object object);
}
