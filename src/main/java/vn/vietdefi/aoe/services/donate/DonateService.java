package vn.vietdefi.aoe.services.donate;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class DonateService implements IDonateService{
    @Override
    public JsonObject donateMatch(long sender, long star, long match_id) {
        try {
            JsonObject response = AoeServices.profileService.getUserProfileByUserId(sender);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return BaseResponse.createFullMessageResponse(10, "profile_not_found");
            }
            JsonObject profile = response.getAsJsonObject("data");
            String username = profile.get("username").getAsString();
            //Lay thong tin tran dau
            response = AoeServices.matchService.getById(match_id);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return BaseResponse.createFullMessageResponse(11, "match_not_found");
            }
            JsonObject match = response.getAsJsonObject("data");
            //Tru sao trong tai khoan message.sender
            response = AoeServices.starService.exchangeStar(-star,
                    StarConstant.SERVICE_DONATE_MATCH,
                    username, 0);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return BaseResponse.createFullMessageResponse(12, "exchange_star_failed");
            }
            //Tao giao dich donate
            JsonObject starTransaction = response.getAsJsonObject("data");
            JsonObject data = new JsonObject();
            data.addProperty("user_id", starTransaction.get("user_id").getAsString());
            data.addProperty("username", starTransaction.get("username").getAsString());
            data.addProperty("nick_name", profile.get("nick_name").getAsString());
            data.addProperty("phone", starTransaction.get("username").getAsString());
            data.addProperty("amount", star);
            data.addProperty("match_id", match.get("id").getAsLong());
            data.addProperty("create_time", starTransaction.get("create_time").getAsLong());
            data.addProperty("star_transaction_id", starTransaction.get("id").getAsLong());
            response = createDonateMatch(data);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return BaseResponse.createFullMessageResponse(33, "create_donate_failed");
            }
            JsonObject donate = response.getAsJsonObject("data");
            AoeServices.starService.updateReferId(starTransaction.get("id").getAsLong(),
                    donate.get("id").getAsLong());
            return BaseResponse.createFullMessageResponse(0, "success", donate);
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject createDonateMatch(JsonObject data) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            bridge.insertObjectToDB("aoe_donate_match", data);
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
            String query = "SELECT * FROM aoe_donate_match WHERE id = ?";
            JsonObject data = bridge.queryOne(query, id);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject donateGamer(long sender, long star, long gamer_id) {
        //Lay profile message.sender
        JsonObject response =  AoeServices.profileService.getUserProfileByUserId(sender);
        if(!BaseResponse.isSuccessFullMessage(response)){
            return BaseResponse.createFullMessageResponse(10, "profile_not_found");
        }
        JsonObject profile = response.getAsJsonObject("data");
        String username = profile.get("username").getAsString();
        //Lay thong tin gamer
        response = AoeServices.gamerService.getGamerByUserId(gamer_id);
        if(!BaseResponse.isSuccessFullMessage(response)){
            return BaseResponse.createFullMessageResponse(11, "gamer_not_found");
        }
        JsonObject gamer = response.getAsJsonObject("data");
        //Tru sao trong tai khoan message.sender
        response = AoeServices.starService.exchangeStar(-star,
                StarConstant.SERVICE_DONATE_GAMER,
                username, 0);
        if (!BaseResponse.isSuccessFullMessage(response)) {
            return BaseResponse.createFullMessageResponse(12, "exchange_star_failed");
        }
        //Tao giao dich donate
        JsonObject starTransaction = response.getAsJsonObject("data");
        JsonObject data = new JsonObject();
        data.addProperty("user_id", starTransaction.get("user_id").getAsString());
        data.addProperty("username", starTransaction.get("username").getAsString());
        data.addProperty("nick_name", profile.get("nick_name").getAsString());
        data.addProperty("phone", starTransaction.get("username").getAsString());
        data.addProperty("amount", star);
        data.addProperty("gamer_id", gamer.get("user_id").getAsLong());
        data.addProperty("create_time", starTransaction.get("create_time").getAsLong());
        data.addProperty("star_transaction_id", starTransaction.get("id").getAsLong());
        response = createDonateGamer(data);
        if(!BaseResponse.isSuccessFullMessage(response)){
            return BaseResponse.createFullMessageResponse(13, "create_donate_failed");
        }
        JsonObject donate = response.getAsJsonObject("data");
        //Cap nhat lai refer_id cho giao dich tru sao
        AoeServices.starService.updateReferId(starTransaction.get("id").getAsLong(),
                donate.get("id").getAsLong());
        return BaseResponse.createFullMessageResponse(0, "success", donate);
    }

    @Override
    public JsonObject createDonateGamer(JsonObject data) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            bridge.insertObjectToDB("aoe_donate_gamer", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateDonateGamer(JsonObject data) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            bridge.updateObjectToDb("aoe_donate_gamer", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getDonateGamerById(long id) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_donate_gamer WHERE id = ?";
            JsonObject data = bridge.queryOne(query, id);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject donateCaster(long sender, long star, long caster_id) {
        JsonObject response =  AoeServices.profileService.getUserProfileByUserId(sender);
        if(!BaseResponse.isSuccessFullMessage(response)){
            return BaseResponse.createFullMessageResponse(50, "profile_not_found");
        }
        JsonObject profile = response.getAsJsonObject("data");
        String username = profile.get("username").getAsString();
        //Lay thong tin gamer
        response = AoeServices.casterService.getCasterByUserId(caster_id);
        if(!BaseResponse.isSuccessFullMessage(response)){
            return BaseResponse.createFullMessageResponse(41, "caster_not_found");
        }
        JsonObject caster = response.getAsJsonObject("data");
        //Tru sao trong tai khoan message.sender
        response = AoeServices.starService.exchangeStar(-star,
                StarConstant.SERVICE_DONATE_GAMER,
                username, 0);
        if (!BaseResponse.isSuccessFullMessage(response)) {
            return BaseResponse.createFullMessageResponse(31, "exchange_star_failed");
        }
        //Tao giao dich donate
        JsonObject starTransaction = response.getAsJsonObject("data");
        JsonObject data = new JsonObject();
        data.addProperty("user_id", starTransaction.get("user_id").getAsString());
        data.addProperty("username", starTransaction.get("username").getAsString());
        data.addProperty("nick_name", profile.get("nick_name").getAsString());
        data.addProperty("phone", starTransaction.get("username").getAsString());
        data.addProperty("amount", star);
        data.addProperty("caster_id", caster.get("user_id").getAsLong());
        data.addProperty("create_time", starTransaction.get("create_time").getAsLong());
        data.addProperty("star_transaction_id", starTransaction.get("id").getAsLong());
        response = createDonateCaster(data);
        if(!BaseResponse.isSuccessFullMessage(response)){
            return BaseResponse.createFullMessageResponse(32, "create_donate_failed");
        }
        JsonObject donate = response.getAsJsonObject("data");
        //Cap nhat lai refer_id cho giao dich tru sao
        AoeServices.starService.updateReferId(starTransaction.get("id").getAsLong(),
                donate.get("id").getAsLong());
        return BaseResponse.createFullMessageResponse(0, "success", donate);
    }

    @Override
    public JsonObject createDonateCaster(JsonObject data) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            bridge.insertObjectToDB("aoe_donate_caster", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateDonateCaster(JsonObject data) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            bridge.updateObjectToDb("aoe_donate_caster", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getDonateCasterById(long id) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_donate_caster WHERE id = ?";
            JsonObject data = bridge.queryOne(query, id);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }catch (Exception e){
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
