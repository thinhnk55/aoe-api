package vn.vietdefi.aoe.services.statistic;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class StatisticService implements IStatisticService {
    @Override
    public JsonObject updateStatistic() {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject response = new JsonObject();
            response.add("donate",AoeServices.donateService.statisticTotalDonate().get("data"));
            response.add("user",AoeServices.userService.statistic().getAsJsonObject().get("data"));
            response.add("match",AoeServices.matchService.statistic().getAsJsonObject().get("data"));
            DebugLogger.info("{}", response);
            return BaseResponse.createFullMessageResponse(0, "success",response);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
