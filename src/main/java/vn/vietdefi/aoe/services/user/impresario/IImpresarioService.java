package vn.vietdefi.aoe.services.user.impresario;

import com.google.gson.JsonObject;

public interface IImpresarioService {
    JsonObject createImp(JsonObject json);
    JsonObject updateImp(JsonObject json, long id);
    JsonObject deleteImp(long id);
    JsonObject getImp(long id);
    JsonObject getAllImp(JsonObject json);
}
