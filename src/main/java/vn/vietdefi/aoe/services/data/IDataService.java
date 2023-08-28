package vn.vietdefi.aoe.services.data;

import com.google.gson.JsonObject;

public interface IDataService {
    JsonObject createData(String name, JsonObject data);
    JsonObject updateData(String name, JsonObject data);
    JsonObject getData(String name);
}
