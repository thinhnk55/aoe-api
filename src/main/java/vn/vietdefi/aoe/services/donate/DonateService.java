package vn.vietdefi.aoe.services.donate;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class DonateService implements IDonateService{
    @Override
    public JsonObject createDonateMatch(JsonObject data) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            bridge.insertObjectToDB("aoe_match_donate", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getDonateMatchById(long id) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_match_donate WHERE id = ?";
            JsonObject data = bridge.queryOne(query, id);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject createDonateGamer(JsonObject data) {
        return null;
    }
}
