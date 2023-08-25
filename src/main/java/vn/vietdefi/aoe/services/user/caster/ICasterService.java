package vn.vietdefi.aoe.services.user.caster;


import com.google.gson.JsonObject;

public interface ICasterService {
    JsonObject createCaster(JsonObject json);
    JsonObject updateCaster(JsonObject json);
    JsonObject getCasterByUserId(long casterId);
    JsonObject getPartialCaster(long casterId);
    JsonObject listCaster(long page, long recordPerPage);
    JsonObject casterUpdateStatistic(long id);
    JsonObject deleteCaster(String nickname);

}
