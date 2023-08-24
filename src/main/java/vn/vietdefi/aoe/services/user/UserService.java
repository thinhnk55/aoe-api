package vn.vietdefi.aoe.services.user;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.event.EventConstants;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class UserService implements  IUserService{
    @Override
    public long getTotalNewUserForWeek() {
        return 0;
    }

    @Override
    public long getTotalUser() {
        return 0;
    }

    @Override
    public JsonObject statistic() {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT COUNT(*) as total_registered_user  FROM user";
            JsonObject result = new JsonObject();
            result.addProperty("total_registered_user",bridge.queryInteger(query));
            query = "SELECT COUNT(*) as total_new_user_this_week FROM user WHERE create_time >= ?";
            long time = System.currentTimeMillis() - 604800000L; // thoi gian trong 1 tuan
            result.addProperty("total_new_user_this_week",bridge.queryInteger(query,time));
            return BaseResponse.createFullMessageResponse(0, "success",result);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
