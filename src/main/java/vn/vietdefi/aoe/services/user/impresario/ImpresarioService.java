package vn.vietdefi.aoe.services.user.impresario;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.services.vdef.VdefServices;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;
import vn.vietdefi.util.string.StringUtil;

public class ImpresarioService implements IImpresarioService{
    @Override
    public JsonObject createImp(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject detail = new JsonObject();
            detail.add("date_of_birth",json.get("date_of_birth"));
            detail.add("nationality",json.get("nationality"));
            detail.add("address",json.get("address"));
            detail.addProperty("sport","Age of Empires");

            JsonObject insertToImpresario = new JsonObject();
            insertToImpresario.addProperty("avatar", json.get("avatar").getAsString());
            insertToImpresario.addProperty("fullname", json.get("fullname").getAsString());
            insertToImpresario.addProperty("phone_number", json.get("phone_number").getAsString());
            insertToImpresario.add("detail", detail);

            JsonObject createUser = new JsonObject();
            createUser.addProperty("username", json.get("phone_number").getAsString());
            String query = "SELECT id FROM user WHERE username = ?";
            if (bridge.queryExist(query, json.get("phone_number").getAsString()))
                return BaseResponse.createFullMessageResponse(11, "phone_number_used");
            createUser.addProperty("password", StringUtil.generateRandomStringNumberCharacter(12));

            long userid = VdefServices.authService.register(createUser).get("data").getAsJsonObject().get("userid").getAsLong();
            insertToImpresario.addProperty("user_id", userid);
            bridge.insertObjectToDB("impresario", insertToImpresario);
            return BaseResponse.createFullMessageResponse(0,"success",insertToImpresario);

        }catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateImp(JsonObject json, long id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject detail = new JsonObject();
            detail.add("date_of_birth",json.get("date_of_birth"));
            detail.add("nationality",json.get("nationality"));
            detail.add("address",json.get("address"));
            detail.addProperty("sport","Age of Empires");

            String logo = json.get("avatar").getAsString();
            String fullname = json.get("fullname").getAsString();
//            String query = "SELECT id FROM user WHERE username = ?";
//            JsonObject donor =bridge.queryOne(query, json.get("phone_number").getAsLong());
//            if (donor!=null && donor.get("id").getAsLong() != id )
//                return BaseResponse.createFullMessageResponse(10, "phone_number_used");

            String query = "UPDATE impresario SET avatar = ? ,fullname = ?,detail = ? WHERE user_id = ?";
            bridge.update(query,logo,fullname,detail,id);
            return BaseResponse.createFullMessageResponse(0,"success");

        }catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject deleteImp(long id) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE impresario SET is_deleted = 1 WHERE user_id = ? AND is_deleted = 0";
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
    public JsonObject getImp(long id) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM impresario WHERE user_id =?";
            JsonObject imp = bridge.queryOne(query, id);
            if (imp == null)return BaseResponse.createFullMessageResponse(10,"not_found");
            return BaseResponse.createFullMessageResponse(0,"success",imp);
        }
        catch(Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getAllImp(JsonObject json) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long page = json.get("page").getAsInt();
            long offset = (page - 1)*25;
            String query = "SELECT * FROM impresario WHERE is_deleted = 0 " +
                    "LIMIT 25 OFFSET ?";
            JsonArray impresario = bridge.query(query,offset);
            query ="SELECT count(*) FROM donors WHERE is_deleted = 0 ";
            JsonObject result = new JsonObject();
            result.addProperty("total_page",bridge.queryInteger(query)/25 + 1);
            result.add("Impresario",impresario);
            return BaseResponse.createFullMessageResponse(0,"success", result);
        }
        catch(Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
