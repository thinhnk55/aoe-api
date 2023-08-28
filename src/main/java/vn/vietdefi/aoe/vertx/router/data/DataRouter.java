package vn.vietdefi.aoe.vertx.router.data;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.data.DataConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class DataRouter {
    public static void updateContact(RoutingContext rc){
        try{
            String body = rc.body().asString();
            JsonObject data = GsonUtil.toJsonObject(body);
            JsonObject response = AoeServices.dataService.updateData(DataConstant.aoe_contact, data);
            if(BaseResponse.isSuccessFullMessage(response)){
                DataCache.AOE_CONTACT = response.toString();
            }
            rc.response().end(response.toString());
        }catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1,"system_error").toString());
        }
    }

    public static void getContact(RoutingContext rc){
        try{
            if(DataCache.AOE_CONTACT == null) {
                JsonObject response = AoeServices.dataService.getData(DataConstant.aoe_contact);
                if(BaseResponse.isSuccessFullMessage(response)){
                    DataCache.AOE_CONTACT = response.toString();
                    rc.response().end(DataCache.AOE_CONTACT);
                }else{
                    JsonObject data = new JsonObject();
                    data.addProperty("phone", "");
                    data.addProperty("facebook", "");
                    response = AoeServices.dataService.createData(DataConstant.aoe_contact, data);
                    if(BaseResponse.isSuccessFullMessage(response)){
                        DataCache.AOE_CONTACT = response.toString();
                        rc.response().end(DataCache.AOE_CONTACT);
                    }else{
                        rc.response().end(response.toString());
                    }
                }
            }else{
                rc.response().end(DataCache.AOE_CONTACT);
            }
        }catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            rc.response().end(BaseResponse.createFullMessageResponse(1,"system_error").toString());
        }
    }
}
