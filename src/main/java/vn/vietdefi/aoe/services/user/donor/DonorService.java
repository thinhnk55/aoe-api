package vn.vietdefi.aoe.services.user.donor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;
import vn.vietdefi.util.string.StringUtil;

public class DonorService implements IDonorService {
    @Override
    public JsonObject createDonor(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject detail = new JsonObject();
            detail.add("website_link",json.get("website_link"));
            detail.add("facebook_link",json.get("facebook_link"));
            detail.add("youtube_link",json.get("youtube_link"));
            detail.add("tiktok_link",json.get("tiktok_link"));

            JsonObject insertToDonors = new JsonObject();
            insertToDonors.addProperty("logo", json.get("logo").getAsString());
            insertToDonors.addProperty("fullname", json.get("fullname").getAsString());
            insertToDonors.addProperty("total_donated", json.get("total_donated").getAsLong());
            insertToDonors.addProperty("phone_number", json.get("phone_number").getAsString());
            insertToDonors.add("detail", detail);

            JsonObject createUser = new JsonObject();
            createUser.addProperty("username", json.get("phone_number").getAsString());
            String query = "SELECT id FROM user WHERE username = ?";
            if (bridge.queryExist(query, json.get("phone_number").getAsString()))
                return BaseResponse.createFullMessageResponse(11, "phone_number_used");
            createUser.addProperty("password", StringUtil.generateRandomStringNumberCharacter(12));

            long userid = VdefServices.authService.register(createUser).get("data").getAsJsonObject().get("userid").getAsLong();
            insertToDonors.addProperty("user_id", userid);
            bridge.insertObjectToDB("donors", insertToDonors);
            return BaseResponse.createFullMessageResponse(0,"success",insertToDonors);

        }catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateDonor(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject detail = new JsonObject();
            detail.add("website_link",json.get("website_link"));
            detail.add("facebook_link",json.get("facebook_link"));
            detail.add("youtube_link",json.get("youtube_link"));
            detail.add("tiktok_link",json.get("tiktok_link"));

            String logo = json.get("logo").getAsString();
            String fullname = json.get("fullname").getAsString();
            long total = json.get("total_donated").getAsLong();
            String phoneNUmber = json.get("phone_number").getAsString();
            long id = json.get("user_id").getAsLong();

            String query = "SELECT id FROM user WHERE username = ?";
            JsonObject donor =bridge.queryOne(query, json.get("phone_number").getAsLong());
            if (donor!=null && donor.get("id").getAsLong() != id )
                return BaseResponse.createFullMessageResponse(10, "phone_number_used");

            query = "UPDATE donors SET logo = ? ,fullname = ?, phone_number = ? ,detail = ?, total_donated = ? WHERE user_id = ?";
            bridge.update(query,logo,fullname,phoneNUmber,detail,total,id);
            return BaseResponse.createFullMessageResponse(0,"success");

        }catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject deleteDonor(long id) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE donors SET is_deleted = 1 WHERE user_id = ? AND is_deleted = 0";
            int check = bridge.update(query,id);
            if (check == 0 )return BaseResponse.createFullMessageResponse(10,"not_found");
            return BaseResponse.createFullMessageResponse(0,"success");
        }
        catch(Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }

    }

    @Override
    public JsonObject getDonor(long id) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM donors WHERE user_id =?";
            JsonObject imp = bridge.queryOne(query, id);
            if (imp == null)return BaseResponse.createFullMessageResponse(10,"not_found");
            return BaseResponse.createFullMessageResponse(0,"success",bridge.queryOne(query, id));
        }
        catch(Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }

    }


    public JsonObject getDonors(JsonObject json) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long page = json.get("page").getAsInt();
            long offset = (page - 1)*25;
            String query = "SELECT * FROM donors WHERE is_deleted = 0 " +
                    "LIMIT 25 OFFSET ?";
            JsonArray Donors = bridge.query(query,offset);
            query ="SELECT count(*) FROM donors WHERE is_deleted = 0 ";
            JsonObject result = new JsonObject();
                   result.addProperty("total_page",bridge.queryInteger(query)/25 + 1);
                   result.add("Donors",Donors);
            return BaseResponse.createFullMessageResponse(0,"success", result);
        }
        catch(Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
