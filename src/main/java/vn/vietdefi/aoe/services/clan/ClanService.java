package vn.vietdefi.aoe.services.clan;

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
            String clanFullName = json.get("clan_fullname").getAsString();
            String query = "SELECT clan_name FROM clan WHERE clan_name = ?";
            if(bridge.queryExist(query,clanName)) return BaseResponse.createFullMessageResponse(12,"clan_name_exist");
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

            query = "INSERT INTO clan(clan_name,clan_fullname,avatar,create_day,founder,owner_unit,sport,detail_info,status) VALUES(?,?,?,?,?,?,?,?,?)";
            bridge.update(query, clanName, clanFullName, avatar, createDay, founder, ownerUnit, sport, detail,0);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
           return BaseResponse.createFullMessageResponse(1,"system_error");
        }
    }
    public JsonObject updateClan(long clanId, JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT clan_id FROM clan WHERE clan_id = ?";
            if (!bridge.queryExist(query, clanId)) return BaseResponse.createFullMessageResponse(13, "clan_not_exist");

            String clanName = json.get("clan_name").getAsString();
            query = "SELECT clan_id FROM clan WHERE clan_name = ?";
            JsonObject check = bridge.queryOne(query, clanName);
            if (check != null && check.get("clan_id").getAsLong() != clanId)
                return BaseResponse.createFullMessageResponse(12, "clan_exist");
            String clanFullName = json.get("clan_fullname").getAsString();
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

            query = "UPDATE clan SET clan_name = ?,avatar = ?,create_day = ?,founder = ?,owner_unit = ?,sport = ?,detail_info = ?,clan_fullname = ? WHERE clan_id = ?";
            bridge.update(query, clanName, avatar, createDay, founder, ownerUnit, sport, detail, clanFullName, clanId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
    @Override
    public JsonObject getClan(long clanId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT `avatar`,`clan_name` FROM clan WHERE status = 0";
            return bridge.queryOne(query, clanId);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject getInfoClan(long clanId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT  * FROM clan WHERE clan_id = ? AND status = 0";
            JsonObject data = bridge.queryOne(query, clanId);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }

    }

}
