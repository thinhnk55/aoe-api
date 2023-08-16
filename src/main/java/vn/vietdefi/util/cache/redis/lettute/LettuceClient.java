package vn.vietdefi.util.cache.redis.lettute;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.util.cache.redis.IRedisClient;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class LettuceClient implements IRedisClient {
    private static LettuceClient ins = null;

    private LettuceClient() {
        init();
    }

    public static LettuceClient instance() {
        if (ins == null) {
            ins = new LettuceClient();
        }
        return ins;
    }

    private final String REDIS_CONFIG_FILE = "config/redis.properties";
    private String HOST = "localhost";
    private String PASSWORD = "";
    private int PORT;

    private RedisClient redisClient;
    private StatefulRedisConnection connection;

    public void init() {
        try {
            InputStream input = new FileInputStream(REDIS_CONFIG_FILE);
            Properties prop = new Properties();
            prop.load(input);
            HOST = prop.getProperty("host");
            PASSWORD = prop.getProperty("password");
            PORT = Integer.parseInt(prop.getProperty("port"));
            RedisURI uri = RedisURI.Builder.redis(HOST, PORT).withPassword(PASSWORD.toCharArray()).withTimeout(Duration.ofSeconds(2)).build();
            redisClient = RedisClient.create(uri);
            connection = redisClient.connect();
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }


    public StatefulRedisConnection getConnection() {
        return connection;
    }

    public long llen(String key) {
        RedisCommands<String, String> commands = connection.sync();
        return commands.llen(key);
    }

    public String rpop(String key) {
        RedisCommands<String, String> commands = connection.sync();
        String value = commands.rpop(key);
        return value;
    }

    public String lpop(String key) {
        RedisCommands<String, String> commands = connection.sync();
        String value = commands.lpop(key);
        return value;
    }

    public void rpush(String key, String value) {
        RedisCommands<String, String> commands = connection.sync();
        commands.rpush(key, value);
    }

    public void lpush(String key, String value) {
        RedisCommands<String, String> commands = connection.sync();
        commands.lpush(key, value);
    }

    public String set(String key, String value) {
        RedisCommands<String, String> commands = connection.sync();
        return commands.set(key, value);
    }

    public void del(String key) {
        RedisCommands<String, String> commands = connection.sync();
        commands.del(key);
    }

    public void setAsync(String key, String value) {
        RedisAsyncCommands<String, String> commands = connection.async();
        commands.set(key, value);
    }

    public void setExpire(String key, String value, int expire) {
        RedisCommands<String, String> commands = connection.sync();
        commands.setex(key, expire, value);
    }

    public String get(String key) {
        try {
            RedisCommands<String, String> commands = connection.sync();
            String value = commands.get(key);
            return value;
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return null;
        }
    }

    public void publish(String channel, String message) {
        try {
            RedisCommands<String, String> commands = connection.sync();
            commands.publish(channel, message);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
        }
    }

    @Override
    public List<String> getList(String key, long from, long to) {
        RedisCommands<String, String> commands = connection.sync();
        List<String> list = commands.lrange(key, from, to);
        return list;
    }

    @Override
    public void insertList(String key, String data, int expire, int maxSize) {
        RedisCommands<String, String> commands = connection.sync();
        commands.lpush(key, data);
        if (expire > 0) {
            commands.expire(key, expire);
        }
        if (maxSize > 0 && commands.llen(key) > maxSize) {
            commands.rpop(key);
        }
    }

    @Override
    public long generateId(String key) {
        RedisCommands<String, String> commands = connection.sync();
        return commands.incr(key);
    }

    @Override
    public Object get(String key, Class clazz) {
        try {
            Object obj = null;
            RedisCommands<String, String> commands = connection.sync();
            String value = commands.get(key);
            if (value != null) {
                obj = GsonUtil.toJsonObject(value, clazz);
            }
            return obj;
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return null;
        }
    }

    @Override
    public String set(String key, Object object) {
        String value = GsonUtil.toJsonString(object);
        RedisCommands<String, String> commands = connection.sync();
        return commands.set(key, value);
    }

    public StatefulRedisPubSubConnection<String, String> connectPubSub() {
        StatefulRedisPubSubConnection<String, String> subConnection = redisClient.connectPubSub();
        return subConnection;
    }
}
