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
            JsonObject response = AoeServices.donateService.statisticTotalDonate().getAsJsonObject("data");
            JsonObject user = AoeServices.userService.statistic().getAsJsonObject("data");
            JsonObject match = AoeServices.matchService.statistic().getAsJsonObject("data");
            response.add("total_registered_user",user.get("total_registered_user"));
            response.add("total_new_user_this_week",user.get("total_new_user_this_week"));
            response.add("total_match_complete",match.get("total_match_complete"));
            response.add("total_league_complete",AoeServices.leagueService.totalLeagueComplete().get("data").getAsJsonObject().get("total_league_complete"));
            DebugLogger.info("{}", response);
            return BaseResponse.createFullMessageResponse(0, "success",response);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
