package vn.vietdefi.aoe.services.clan;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class ClanService implements IClanService{
    public JsonObject createClan(JsonObject json){
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String clanName = json.get("clan_name").getAsString();
            String query = "SELECT clan_name FROM aoe_clan WHERE clan_name = ?";
            if(bridge.queryExist(query,clanName)) {
                return BaseResponse.createFullMessageResponse(12,"clan_name_exist");
            }
            bridge.insertObjectToDB("aoe_clan",json);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
           return BaseResponse.createFullMessageResponse(1,"system_error");
        }
    }

    public JsonObject updateClan(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long clanId = data.get("id").getAsLong();
            String clanName = data.get("clan_name").getAsString();
            String query = "SELECT id from aoe_clan WHERE clan_name = ? AND id != ?";
            boolean isClanNameExist = bridge.queryExist(query, clanName, clanId);
            if (isClanNameExist)
                return BaseResponse.createFullMessageResponse(12, "clan_name_exist");
            bridge.updateObjectToDb("aoe_clan", data);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getListClan(long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject result = new JsonObject();
            long totalRow = 0;
            //Total record
            String query = "SELECT COALESCE(COUNT(id),0) FROM aoe_clan WHERE status = ?";
            totalRow = bridge.queryLong(query, 0);
            //Data return
            query = "SELECT * from aoe_clan where status = ? LIMIT ? OFFSET ?";
            JsonArray clans = bridge.query(query, 0, recordPerPage, (page - 1) * recordPerPage);
            //Total page
            long totalPage = totalRow % recordPerPage != 0 ? totalRow/recordPerPage + 1 : totalRow/recordPerPage;

            result.addProperty("total_page", totalPage);
            result.add("clans", clans);
            return BaseResponse.createFullMessageResponse(0, "success", result);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject getClanInfo(long clanId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT  * FROM aoe_clan WHERE id = ? AND status = 0";
            JsonObject data = bridge.queryOne(query, clanId);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }

    }

}
