package vn.vietdefi.aoe.services.user.impresario;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.api.services.auth.UserConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;
import vn.vietdefi.util.string.StringUtil;

public class ImpresarioService implements IImpresarioService{
    @Override
    public JsonObject createImpresario(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String phone = data.get("phone").getAsString();
            String query = "SELECT * FROM aoe_impresario WHERE phone = ?";
            JsonObject response = bridge.queryOne(query, phone);
            if (response != null)
                return BaseResponse.createFullMessageResponse(13, "phone_number_exist");
            response = ApiServices.authService.get(phone);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                String password = StringUtil.generateRandomStringNumberCharacter(8);
                response = AoeServices.aoeAuthService.register(phone, password, UserConstant.ROLE_USER, UserConstant.STATUS_ACCOUNT_GENERATE);
                if (!BaseResponse.isSuccessFullMessage(response)) {
                    return response;
                }
            } else {
                long userId = response.getAsJsonObject("data").get("id").getAsLong();
                String password = StringUtil.generateRandomStringNumberCharacter(8);
                ApiServices.authService.updatePassword(userId, password);
            }
            JsonObject user = response.getAsJsonObject("data");
            data.addProperty("user_id", user.get("id").getAsLong());

            bridge.insertObjectToDB("aoe_impresario", "user_id", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        }catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject updateImpresario(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            bridge.updateObjectToDb("aoe_impresario", "user_id", data);
            return BaseResponse.createFullMessageResponse(0,"success");
        }catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject deleteImpresario(long id) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "DELETE FROM aoe_impresario WHERE user_id = ?";
            bridge.update(query, id);
            return BaseResponse.createFullMessageResponse(0,"success");
        }
        catch(Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getImpresario(long id) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_impresario WHERE user_id = ?";
            JsonObject data = bridge.queryOne(query, id);
            if (data == null)
                return BaseResponse.createFullMessageResponse(10,"impresario_not_found");
            return BaseResponse.createFullMessageResponse(0,"success", data);
        }
        catch(Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getAllImpresario(long page, long recordPerPage) {
        try{
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            String query = "SELECT * FROM aoe_impresario LIMIT ? OFFSET ?";
            JsonArray impresario = bridge.query(query, recordPerPage, offset);
            JsonObject data = new JsonObject();
            data.add("impresario", impresario);
            return BaseResponse.createFullMessageResponse(0,"success", data);
        }
        catch(Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
