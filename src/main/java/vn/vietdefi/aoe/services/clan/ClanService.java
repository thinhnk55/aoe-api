package vn.vietdefi.aoe.services.clan;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class ClanService implements IClanService{
    public JsonObject createClan(JsonObject data){
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String clanName = data.get("nick_name").getAsString();
            String query = "SELECT nick_name FROM aoe_clan WHERE nick_name = ?";
            if(bridge.queryExist(query,clanName)) {
                return BaseResponse.createFullMessageResponse(12,"clan_name_exist");
            }
            bridge.insertObjectToDB("aoe_clan",data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
           return BaseResponse.createFullMessageResponse(1,"system_error");
        }
    }

    public JsonObject updateClan(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            int x = bridge.updateObjectToDb("aoe_clan", data);
            if(x == 0) {
                return BaseResponse.createFullMessageResponse(10, "update_failure");
            }else{
                return BaseResponse.createFullMessageResponse(0, "success");
            }
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
            //Data return
            String query = "SELECT * from aoe_clan where state = ? LIMIT ? OFFSET ?";
            JsonArray clans = bridge.query(query, 0, recordPerPage, (page - 1) * recordPerPage);
            result.add("clans", clans);
            return BaseResponse.createFullMessageResponse(0, "success", result);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject getClanById(long clanId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT  * FROM aoe_clan WHERE id = ?";
            JsonObject data = bridge.queryOne(query, clanId);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getClanByNickName(String nickName) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT  * FROM aoe_clan WHERE nick_name = ?";
            JsonObject data = bridge.queryOne(query, nickName);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    /*These function user for TEST only. In real situation these actions is prohibited*/
    public JsonObject deleteClan(long clanId){
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "DELETE FROM aoe_clan WHERE id = ?";
            bridge.update(query, clanId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
