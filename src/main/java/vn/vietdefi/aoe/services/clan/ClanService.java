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
            String clanName = json.get("clan_name").getAsString();
            insertToDb.addProperty("clan_name",clanName);
            insertToDb.addProperty("clan_fullname",json.get("clan_fullname").getAsString());
            String query = "SELECT clan_name FROM aoe_clan WHERE clan_name = ?";
            if(bridge.queryExist(query,clanName)) {
                return BaseResponse.createFullMessageResponse(12,"clan_name_exist");
            }
            insertToDb.add("detail_info",createDetail(json));
            insertToDb.addProperty("avatar",json.get("avatar").getAsString());
            insertToDb.addProperty("founder",json.get("founder").getAsString());
            insertToDb.addProperty("owner_unit",json.get("owner_unit").getAsString());
            insertToDb.addProperty("sport",json.get("sport").getAsString());
            insertToDb.addProperty("create_day",json.get("create_day").getAsLong());
            bridge.insertObjectToDB("aoe_clan",insertToDb);
            return BaseResponse.createFullMessageResponse(0, "success");
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
            String clanName = json.get("clan_name").getAsString();
            String query = "SELECT id from aoe_clan WHERE clan_name = ? AND id != ?";
            boolean isClanNameExist = bridge.queryExist(query, clanName, clanId);
            if (isClanNameExist)
                return BaseResponse.createFullMessageResponse(12, "clan_name_exist");

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

            query = "UPDATE aoe_clan SET clan_name = ?,avatar = ?,create_day = ?,founder = ?,owner_unit = ?,sport = ?,detail_info = ?,clan_fullname = ? WHERE id = ?";
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

    public JsonObject getClanById(long clanId) {
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
