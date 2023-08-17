package vn.vietdefi.aoe.services.gamer;

import com.google.gson.JsonObject;

public interface IGamerService {
    JsonObject create(JsonObject json);
    JsonObject updateInfo(JsonObject json);
    JsonObject deleteAccountant(long userid);
}
