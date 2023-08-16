package vn.vietdefi.websocket.services;

import com.google.gson.JsonObject;

public interface IWebsocketUserService {
    JsonObject getUserData(long userId, String token);
}
