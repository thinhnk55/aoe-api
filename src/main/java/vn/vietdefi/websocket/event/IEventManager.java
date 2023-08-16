package vn.vietdefi.websocket.event;

import java.util.concurrent.Executor;

public interface IEventManager extends IEventDispatcher {
    void setThreadPoolSize(int i);

    void dispatchImmediateEvent(IEvent event);

    Executor getThreadPool();
}
