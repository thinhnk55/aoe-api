package vn.vietdefi.websocket.event.imp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vietdefi.websocket.event.IEvent;
import vn.vietdefi.websocket.event.IEventListener;
import vn.vietdefi.websocket.event.IEventManager;
import vn.vietdefi.websocket.event.IEventType;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;


public final class CCGEventManager implements IEventManager {
    private final ThreadPoolExecutor threadPool;
    private final Map listenersByEvent = new ConcurrentHashMap();
    private final Logger logger = LoggerFactory.getLogger(CCGEventManager.class);

    String name = "CCGEventManager";
    private final int corePoolSize;
    private final int maxPoolSize;
    private final int threadKeepAliveTime;
    private static CCGEventManager ins = null;

    public static CCGEventManager instance() {
        if (ins == null) {
            ins = new CCGEventManager();
        }
        return ins;
    }

    private CCGEventManager() {
        corePoolSize = 20;
        maxPoolSize = 30;
        threadKeepAliveTime = 60;
        threadPool = new ThreadPoolExecutor(corePoolSize, maxPoolSize, threadKeepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue());
    }

    public void init(Object o) {
        logger.info((new StringBuilder(String.valueOf(name))).append(" initialized").toString());
    }

    public void destroy(Object o) {
        listenersByEvent.clear();
        logger.info((new StringBuilder(String.valueOf(name))).append(" shut down.").toString());
    }

    public void setThreadPoolSize(int poolSize) {
        threadPool.setCorePoolSize(poolSize);
    }

    public synchronized void addEventListener(IEventType type, IEventListener listener) {
//        DebugLogger.info("event: {} ", type);
        Set listeners = (Set) listenersByEvent.get(type);
        if (listeners == null) {
            listeners = new CopyOnWriteArraySet();
            listenersByEvent.put(type, listeners);
        }
        listeners.add(listener);
    }

    public boolean hasEventListener(IEventType type) {
        boolean found = false;
        Set listeners = (Set) listenersByEvent.get(type);
        if (listeners != null && listeners.size() > 0)
            found = true;
        return found;
    }

    public synchronized void removeEventListener(IEventType type, IEventListener listener) {
        Set listeners = (Set) listenersByEvent.get(type);
        if (listeners != null)
            listeners.remove(listener);
    }

    public void dispatchEvent(IEvent event) {
        Set listeners = (Set) listenersByEvent.get(event.getType());
        if (listeners != null && listeners.size() > 0) {
            IEventListener listener;
            for (Iterator iterator = listeners.iterator(); iterator.hasNext(); threadPool.execute(new EventRunner(listener, event)))
                listener = (IEventListener) iterator.next();
        }
    }

    public void dispatchImmediateEvent(IEvent event) {
        Set listeners = (Set) listenersByEvent.get(event.getType());
        if (listeners != null && listeners.size() > 0) {
            IEventListener listener;
            for (Iterator iterator = listeners.iterator(); iterator.hasNext(); run(listener, event))
                listener = (IEventListener) iterator.next();

        }
    }

    private void run(IEventListener listener, IEvent event) {
        try {
            listener.handleEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Executor getThreadPool() {
        return this.threadPool;
    }

    private static final class EventRunner
            implements Runnable {

        private final IEventListener listener;
        private final IEvent event;

        public EventRunner(IEventListener listener, IEvent event) {
            this.listener = listener;
            this.event = event;
        }

        public void run() {
            try {
                listener.handleEvent(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
