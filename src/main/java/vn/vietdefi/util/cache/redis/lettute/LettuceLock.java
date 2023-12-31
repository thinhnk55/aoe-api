package vn.vietdefi.util.cache.redis.lettute;

import io.lettuce.core.SetArgs;
import io.lettuce.core.api.sync.RedisCommands;
import vn.vietdefi.util.cache.redis.IRedisLock;
import vn.vietdefi.util.log.DebugLogger;

import java.util.UUID;

public class LettuceLock implements IRedisLock {
    /**
     * Lock key
     */
    private final String key;
    /**
     * Lock timeout time in milliseconds
     */
    private int expire = 5000;
    /**
     * Gets the lock waiting time in milliseconds
     */
    private int timeout = 3000;
    /**
     * Does it possess locks?
     */
    private volatile boolean locked = false;
    /**
     * Unique Identification
     */
    private final UUID uuid;

    /**
     * Thread Waiting Time
     */
    private static final int DEFAULT_ACQUIRY_RESOLUTION_MILLIS = 100;

    /**
     * lua script to delete key
     */
    public LettuceLock(String key, int timeout, int expire) {
        this.key = key;
        this.timeout = timeout;
        this.expire = expire;
        this.uuid = UUID.randomUUID();
    }

    /**
     * set value
     * Note: This command can only be executed successfully without a key (NX option), and the key has an automatic expiration time (PX attribute).
     * The value of this key is a unique value. This value must be unique on all clients. The value of the same key can not be the same for all the acquirers (competitors).
     *
     * @param value
     * @return
     */
    public String setNX(final String value) {
        SetArgs setParams = new SetArgs();
        setParams.nx();
        setParams.px(this.expire);
        RedisCommands<String, String> commands = LettuceClient.instance().getConnection().sync();
        String result = commands.set(key, value, setParams);
        return result;
    }

    /**
     * Acquisition locks
     *
     * @return
     */
    public synchronized boolean lock() throws InterruptedException {
        long timeout = this.timeout;
        while (timeout > 0) {
            //Get the lock and return to OK represents the success of acquiring the lock.
            if ("OK".equals(this.setNX(this.getLockValue(Thread.currentThread().getId())))) {
                DebugLogger.debug("Lettuce locked: {}", key);
                this.locked = true;
                return true;
            }
            timeout -= DEFAULT_ACQUIRY_RESOLUTION_MILLIS;
            Thread.sleep(DEFAULT_ACQUIRY_RESOLUTION_MILLIS);
        }
        return false;
    }

    /**
     * Release lock
     * Note: Delete by key and unique value
     *
     * @return
     */
    public synchronized void release() {
        if (this.locked) {
            LettuceClient.instance().del(key);
            this.locked = false;
        }
        DebugLogger.debug("Lettuce released {}", key);
    }

    /**
     * Determine whether the current thread continues to have locks
     * Explanation: This method is mainly used to judge that the operation time has exceeded the expiration time of key, and can be used for business rollover.
     *
     * @return
     */
    public boolean checkTimeOut() {
        if (this.locked) {
            String value = LettuceClient.instance().get(this.key);
            if (this.getLockValue(Thread.currentThread().getId()).equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Value of a lock (unique value)
     * Note: Value must be the only value that is primarily for safer release of the lock. When releasing the lock, use a script to tell Redis that only the key exists and the stored value is the same as the value I specified can tell me that the deletion is successful.
     *
     * @param threadId
     * @return
     */
    public String getLockValue(Long threadId) {
        return this.uuid.toString() + "_" + threadId;
    }
}
