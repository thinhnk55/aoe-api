package vn.vietdefi.util.cache.redis;

import vn.vietdefi.util.cache.redis.lettute.LettuceClient;
import vn.vietdefi.util.cache.redis.lettute.LettuceLock;
import vn.vietdefi.util.cache.redis.lettute.LettuceSubscriber;

public class RedisClients {
    private static IRedisClient client = null;
    RedisClients(){
        LettuceClient.instance();
    }

    public static IRedisClient client(){
        if(client == null){
            client = LettuceClient.instance();
        }
        return client;
    }

    public static IRedisSubscriber createSubscribe(String channel, RedisHandler handler){
        IRedisSubscriber subscriber = new LettuceSubscriber(channel, handler);
        return subscriber;
    }

    public static IRedisLock createLock(String key, int timeout, int expire){
        IRedisLock lock = new LettuceLock(key, timeout, expire);
        return lock;
    }
}
