package vn.vietdefi.websocket;

import com.google.gson.JsonObject;
import io.vertx.core.http.ServerWebSocket;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.websocket.event.IEvent;
import vn.vietdefi.websocket.event.IEventParam;
import vn.vietdefi.websocket.event.imp.CCGEvent;
import vn.vietdefi.websocket.event.imp.CCGEventManager;
import vn.vietdefi.websocket.event.imp.CCGEventParam;
import vn.vietdefi.websocket.event.imp.CCGEventType;
import vn.vietdefi.websocket.services.IWebsocketUserService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WebsocketUserManager {
    private static WebsocketUserManager ins = null;
    public ConcurrentHashMap<Long, List<WebsocketUser>> websocketUsers;
    public IWebsocketUserService websocketUserService;

    private WebsocketUserManager() {
        websocketUsers = new ConcurrentHashMap<>();
    }

    public static WebsocketUserManager instance() {
        if (ins == null) {
            ins = new WebsocketUserManager();
        }
        return ins;
    }

    public void init(IWebsocketUserService websocketUserService){
        this.websocketUserService = websocketUserService;
    }

    public void logout(WebsocketUser websocketUser) {
        List<WebsocketUser> users = websocketUsers.get(websocketUser.userId);
        Iterator<WebsocketUser> iterator = users.iterator();
        while (iterator.hasNext()){
            WebsocketUser currentUser = iterator.next();
            if(currentUser.sessionId.equals(websocketUser.sessionId)){
                iterator.remove();
            }
        }
        Map<IEventParam, JsonObject> params = new HashMap<>();
        params.put(CCGEventParam.JSON, websocketUser.toShortJsonObject());
        IEvent event = new CCGEvent(CCGEventType.USER_LOGOUT, params);
        CCGEventManager.instance().dispatchEvent(event);
    }

    public WebsocketUser login(ServerWebSocket serverWebSocket, long userId, String token) {
        JsonObject user = websocketUserService.getUserData(userId, token);
        if (user != null) {
            List<WebsocketUser> users = websocketUsers.get(userId);
            if(users == null){
                users = new LinkedList<>();
                websocketUsers.put(userId, users);
            }
            WebsocketUser websocketUser = new WebsocketUser();
            websocketUser.userId = user.get("id").getAsLong();
            websocketUser.role = user.get("role").getAsInt();
            websocketUser.socket = serverWebSocket;
            users.add(websocketUser);
            serverWebSocket.textMessageHandler(new TextWebsocketHandler(websocketUser));
            serverWebSocket.closeHandler(new CloseWebSocketHandler(websocketUser));
            DebugLogger.debug("{} login at session {}", userId, websocketUser.sessionId);
            Map<IEventParam, JsonObject> params = new HashMap<>();
            params.put(CCGEventParam.JSON, websocketUser.toShortJsonObject());
            IEvent event = new CCGEvent(CCGEventType.USER_LOGIN, params);
            CCGEventManager.instance().dispatchEvent(event);
            return websocketUser;
        }
        return null;
    }

    public void kickUser(long userId) {
        List<WebsocketUser> users = websocketUsers.get(userId);
        if (users != null) {
            for(WebsocketUser user: users) {
                JsonObject websocketResponse = WebsocketResponse.create(WebsocketID.LOGIN, 4);
                user.socket.writeTextMessage(websocketResponse.toString());
                user.socket.close();
            }
            websocketUsers.remove(userId);
        }
    }

    public List<WebsocketUser> getUserById(long userId) {
        return websocketUsers.get(userId);
    }
}