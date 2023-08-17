package vn.vietdefi.aoe.services.caster;


import com.google.gson.JsonObject;

public interface ICasterService {
    JsonObject createCaster(JsonObject json);
    JsonObject updateCaster(long casterId,JsonObject json);
    JsonObject deleteCaster(long casterId);

}
