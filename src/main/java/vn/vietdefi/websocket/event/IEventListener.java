package vn.vietdefi.websocket.event;

public interface IEventListener {
    void handleEvent(IEvent event) throws Exception;
}
