package vn.vietdefi.util.cache.redis.lettute;

import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import vn.vietdefi.util.cache.redis.IRedisSubscriber;
import vn.vietdefi.util.cache.redis.RedisHandler;
import vn.vietdefi.util.log.DebugLogger;

public class LettuceSubscriber implements IRedisSubscriber {
    public String channel;
    public RedisHandler handler;

    public void run() {
        StatefulRedisPubSubConnection<String, String> connection = LettuceClient.instance().connectPubSub();
        connection.addListener(new RedisPubSubListener<String, String>() {
            @Override
            public void message(String channel, String message) {
                handler.onMessage(channel, message);
            }

            @Override
            public void message(String pattern, String channel, String message) {

            }

            @Override
            public void subscribed(String channel, long count) {
                DebugLogger.debug("Lettuce subscribed", channel);
            }

            @Override
            public void psubscribed(String pattern, long count) {

            }

            @Override
            public void unsubscribed(String channel, long count) {
                DebugLogger.debug("Lettuce unsubscribed", channel);
            }

            @Override
            public void punsubscribed(String pattern, long count) {

            }
        });
        RedisPubSubAsyncCommands<String, String> async = connection.async();
        async.subscribe(channel);
    }

    public LettuceSubscriber(String channel, RedisHandler handler) {
        this.handler = handler;
        this.channel = channel;
    }
}
