package vn.vietdefi.websocket.event;

public interface IEventDispatcher {
    void addEventListener(IEventType eventType, IEventListener eventListener);

    boolean hasEventListener(IEventType eventType);

    void removeEventListener(IEventType eventType, IEventListener eventListener);

    void dispatchEvent(IEvent event);
}
