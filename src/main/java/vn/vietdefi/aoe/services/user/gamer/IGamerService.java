package vn.vietdefi.aoe.services.user.gamer;

import com.google.gson.JsonObject;

public interface IGamerService {
    JsonObject create(JsonObject json);
    JsonObject getById(long id);
    JsonObject updateInfo(JsonObject json);
    JsonObject deleteAccountant(long userid);
}
