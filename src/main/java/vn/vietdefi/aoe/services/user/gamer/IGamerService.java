package vn.vietdefi.aoe.services.user.gamer;

import com.google.gson.JsonObject;

public interface IGamerService {
    JsonObject create(JsonObject json);
    JsonObject getGamerByUserId(long user_id);
    JsonObject updateInfo(JsonObject json);
}
