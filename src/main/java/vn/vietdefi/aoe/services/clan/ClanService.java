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
            JsonObject insertToDb = new JsonObject();
            String clanName = json.get("nick_name").getAsString();
            insertToDb.addProperty("nick_name",clanName);
            insertToDb.addProperty("full_name",json.get("full_name").getAsString());
            String query = "SELECT nick_name FROM aoe_clan WHERE nick_name = ?";
            if(bridge.queryExist(query,clanName)) {
                return BaseResponse.createFullMessageResponse(12,"clan_name_exist");
            }
            insertToDb.add("detail",createDetail(json));
            insertToDb.addProperty("avatar",json.get("avatar").getAsString());
            insertToDb.addProperty("founder",json.get("founder").getAsString());
            insertToDb.addProperty("owner_unit",json.get("owner_unit").getAsString());
            insertToDb.addProperty("sport",json.get("sport").getAsString());
            insertToDb.addProperty("create_day",json.get("create_day").getAsLong());
            bridge.insertObjectToDB("aoe_clan",insertToDb);
            return BaseResponse.createFullMessageResponse(0, "success", insertToDb);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
           return BaseResponse.createFullMessageResponse(1,"system_error");
        }
    }

    private JsonObject createDetail(JsonObject json) {
        JsonObject detail = new JsonObject();
        detail.add("facebook_link", json.get("facebook_link"));
        detail.add("tiktok_link", json.get("tiktok_link"));
        detail.add("youtube_link", json.get("youtube_link"));
        detail.add("fanpage_link", json.get("fanpage_link"));
        return detail;
    }

    public JsonObject updateClan(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long clanId = json.get("id").getAsLong();
            String clanName = json.get("nick_name").getAsString();
            String query = "SELECT id from aoe_clan WHERE nick_name = ? AND id != ?";
            boolean isClanNameExist = bridge.queryExist(query, clanName, clanId);
            if (isClanNameExist)
                return BaseResponse.createFullMessageResponse(12, "clan_name_exist");

            String clanFullName = json.get("full_name").getAsString();
            String avatar = json.get("avatar").getAsString();
            long createDay = json.get("create_day").getAsLong();
            String founder = json.get("founder").getAsString();
            String ownerUnit = json.get("owner_unit").getAsString();
            String sport = json.get("sport").getAsString();

            JsonObject detail = new JsonObject();
            detail.addProperty("facebook_link", json.get("facebook_link").getAsString());
            detail.addProperty("tiktok_link", json.get("tiktok_link").getAsString());
            detail.addProperty("youtube_link", json.get("youtube_link").getAsString());
            detail.addProperty("fanpage_link", json.get("fanpage_link").getAsString());

            query = "UPDATE aoe_clan SET nick_name = ?,avatar = ?,create_day = ?,founder = ?,owner_unit = ?,sport = ?,detail = ?,full_name = ? WHERE id = ?";
            bridge.update(query, clanName, avatar, createDay, founder, ownerUnit, sport, detail, clanFullName, clanId);

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
