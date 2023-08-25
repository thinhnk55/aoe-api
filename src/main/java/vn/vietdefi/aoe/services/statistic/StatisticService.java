package vn.vietdefi.aoe.services.statistic;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.aoe.services.statistic.logic.Statistic;
import vn.vietdefi.aoe.services.statistic.logic.StatisticController;
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
            return BaseResponse.createFullMessageResponse(0, "success",response);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject adminCallBackUpdateStatistic() {
        try {
            StatisticController.instance().callBackUpdateStatistic();
            JsonObject response = new JsonObject();
            response.add("statistic",getAllStatistic().get("data"));
            return BaseResponse.createFullMessageResponse(0, "success",response);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject getStatistic() {
        try {
            JsonObject response =new JsonObject();
            Statistic statistic = StatisticController.instance().getStatistic();
            response.addProperty("total_star_donate_for_match",statistic.totalStarDonateForMatch);
            response.addProperty("total_star_donate_for_gamer",statistic.totalStarDonateForGamer);
            response.addProperty("total_star_donate_for_caster",statistic.totalStarDonateForCaster);
            response.addProperty("total_star_donate_for_league",statistic.totalStarDonateForLeague);
            return BaseResponse.createFullMessageResponse(0, "success",response);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject getAllStatistic() {
        try {
            JsonObject response =new JsonObject();
            Statistic statistic = StatisticController.instance().getStatistic();

            response.addProperty("total_registered_user",statistic.totalRegisterUser);
            response.addProperty("total_new_user_this_week",statistic.totalNewUsersThisWeek);
            response.addProperty("total_user_donate",statistic.totalUserDonate);
            response.addProperty("total_star_donate",statistic.totalStarDonate);
            response.addProperty("total_star_donate_for_match",statistic.totalStarDonateForMatch);
            response.addProperty("total_star_donate_for_gamer",statistic.totalStarDonateForGamer);
            response.addProperty("total_star_donate_for_caster",statistic.totalStarDonateForCaster);
            response.addProperty("total_star_donate_for_league",statistic.totalStarDonateForLeague);
            response.addProperty("total_league_complete",statistic.totalLeagueComplete);
            response.addProperty("total_match_complete",statistic.totalMatchComplete);
            return BaseResponse.createFullMessageResponse(0, "success",response);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
