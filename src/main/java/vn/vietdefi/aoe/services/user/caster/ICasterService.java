package vn.vietdefi.aoe.services.user.caster;


import com.google.gson.JsonObject;
import org.apache.commons.lang3.ArrayUtils;

public interface ICasterService {
    JsonObject createCaster(JsonObject json);
    JsonObject updateCaster(long casterId,JsonObject json);
    JsonObject deleteCaster(long casterId);
    JsonObject getInfoCaster(long casterId);
}
