package vn.vietdefi.aoe.services.statistic.logic;

import com.google.gson.JsonObject;
import vn.vietdefi.aoe.services.statistic.IStatisticService;
import vn.vietdefi.aoe.services.statistic.StatisticService;
import vn.vietdefi.common.BaseResponse;

public class StatisticController {
    private static StatisticController ins = null;
    public static StatisticController instance() {
        if (ins == null) {
            ins = new StatisticController();
        }
        return ins;
    }
    private StatisticController(){
        updateStatistic();
        statisticService = new StatisticService();
    }

    Statistic statistic;
    IStatisticService statisticService ;


    public void updateStatistic() {
        if (statistic == null) {
            statistic = UpdateStatistic();
        }
    }

    public void userRegistered(int newUserCount) {
        statistic.updateUserRegistrationCount(newUserCount);
    }

    public void newUsersThisWeek(int newUsersCount) {
        statistic.updateNewUsersThisWeek(newUsersCount);
    }

    public void userDonate(int donatedStars) {
        statistic.updateDonationStats(donatedStars);
    }

    public void tournamentComplete(int tournaments) {
        statistic.updateTournamentStats(tournaments);
    }

    public void championshipComplete(int championships) {
        statistic.updateMatchStats(championships);
    }

    public void starDonateToEntity(int entity, int stars) {
        statistic.updateStarsDonatedToEntities(entity, stars);
    }

    public Statistic UpdateStatistic() {
         JsonObject response = new JsonObject();
         response = statisticService.updateStatistic();
         if (BaseResponse.isSuccessFullMessage(response)){
             return new Statistic(response.get("data").getAsJsonObject());
         }
         return null;
    }
    

}
