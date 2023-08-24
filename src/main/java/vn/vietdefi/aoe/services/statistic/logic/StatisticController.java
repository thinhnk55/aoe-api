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
    }

    Statistic statistic;
    IStatisticService statisticService = new StatisticService();


    public void updateStatistic() {
        if (statistic == null) {
            statistic = UpdateStatistic();
        }
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

    public void championshipComplete() {
        statistic.updateMatchStats();
    }

    public void starDonateToEntity(int entity, long stars) {
        statistic.updateStarsDonatedToEntities(entity, stars);
    }

    public Statistic UpdateStatistic() {
         JsonObject response = statisticService.updateStatistic();
         if (BaseResponse.isSuccessFullMessage(response)){
             return new Statistic(response.get("data").getAsJsonObject());
         }
         return null;
    }
    

}
