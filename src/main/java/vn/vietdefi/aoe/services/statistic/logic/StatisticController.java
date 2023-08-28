package vn.vietdefi.aoe.services.statistic.logic;

import com.google.gson.JsonObject;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.statistic.IStatisticService;
import vn.vietdefi.aoe.services.statistic.StatisticService;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;

import java.util.jar.JarEntry;

public class StatisticController {
    private static StatisticController ins = null;

    public static StatisticController instance() {
        if (ins == null) {
            ins = new StatisticController();

        }
        return ins;
    }

    private StatisticController() {
        StatisticStart();
    }

    Statistic statistic;
    IStatisticService statisticService = new StatisticService();


    public void StatisticStart() {
        JsonObject response = AoeServices.dataService.getData("statistic");
        if (BaseResponse.isSuccessFullMessage(response)) {
            statistic = new Statistic(response.get("data").getAsJsonObject());
        }
        if (statistic == null) {
            statistic = updateStatistic();
        }
    }

    public void callBackUpdateStatistic() {
        statistic = updateStatistic();
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public void userRegistered() {
        statistic.updateUserRegistrationCount();
    }

    public void newUsersThisWeek() {
        statistic.updateNewUsersThisWeek();
    }

    public void userDonate(int donatedStars) {
        statistic.updateDonationStats(donatedStars);
    }

    public void tournamentComplete() {
        statistic.updateTournamentStats();
    }

    public void matchComplete() {
        statistic.updateMatchStats();
    }

    public void starDonateToEntity(int entity, long stars) {
        statistic.updateStarsDonatedToEntities(entity, stars);
    }

    public Statistic updateStatistic() {
        JsonObject response = statisticService.updateStatistic();
        if (BaseResponse.isSuccessFullMessage(response)) {
            statistic = new Statistic(response.get("data").getAsJsonObject());
            JsonObject data = AoeServices.dataService.getData("statistic");
            if (!BaseResponse.isSuccessFullMessage(data)) {
                response = AoeServices.dataService.createData("statistic", response.get("data").getAsJsonObject());
                DebugLogger.info("{}",response);
            } else {
                response = AoeServices.dataService.updateData("statistic", response.get("data").getAsJsonObject());
                DebugLogger.info("{}",response);
            }
        }
        return statistic;

    }
}
