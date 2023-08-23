package vn.vietdefi.aoe.services.user.caster;


import com.google.gson.JsonObject;

public interface ICasterService {
    JsonObject createCaster(JsonObject json);
    JsonObject updateCaster(JsonObject json);
    JsonObject deleteCaster(String nickname);
    JsonObject getCasterByUserId(long user_id);
    JsonObject listCaster(long page, long recordPerPage);
    JsonObject casterUpdateStatistic(long id);
}
