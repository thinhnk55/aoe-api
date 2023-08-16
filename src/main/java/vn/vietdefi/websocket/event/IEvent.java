package vn.vietdefi.websocket.event;

public interface IEvent {
    IEventType getType();

    Object getParameter(IEventParam param);
}
