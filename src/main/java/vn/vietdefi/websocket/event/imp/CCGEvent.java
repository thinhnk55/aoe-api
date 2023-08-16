package vn.vietdefi.websocket.event.imp;

import vn.vietdefi.websocket.event.IEvent;
import vn.vietdefi.websocket.event.IEventParam;
import vn.vietdefi.websocket.event.IEventType;

import java.util.Map;

public class CCGEvent implements IEvent {
    private final IEventType type;
    private final Map<IEventParam, Object> params; // All data

    public CCGEvent(IEventType type) {
        this(type, null);
    }

    public CCGEvent(IEventType type, Map params) {
        this.type = type;
        this.params = params;
    }

    public IEventType getType() {
        return type;
    }

    public Object getParameter(IEventParam id) {
        Object param = null;
        if (params != null)
            param = params.get(id);
        return param;
    }

    public String toString() {
        return String.format("{ %s, Params: %s }", new Object[]{
                type, params == null ? "none" : params.keySet()
        });
    }
}
