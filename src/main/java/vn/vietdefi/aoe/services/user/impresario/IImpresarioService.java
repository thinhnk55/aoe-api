package vn.vietdefi.aoe.services.user.impresario;

import com.google.gson.JsonObject;
import vn.vietdefi.api.services.auth.UserConstant;

public interface IImpresarioService {
    JsonObject createImpresario(JsonObject data);
    JsonObject updateImpresario(JsonObject data);
    JsonObject deleteImpresario(long id);
    JsonObject getImpresario(long id);
    JsonObject getAllImpresario(long page, long recordPerPage);
}
