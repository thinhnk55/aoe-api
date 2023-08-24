package vn.vietdefi.aoe.services.statistic.logic;

import com.google.gson.JsonObject;

import static vn.vietdefi.aoe.services.statistic.logic.StatisticConstant.*;

public class Statistic {
    public long totalUserRegister;
    public long totalNewUsersThisWeek;

    public long totalUserDonate;
    public long totalStarDonate;
    public long totalStarDonateForMatch;
    public long totalStarDonateForLeague;
    public long totalStarDonateForGamer;
    public long totalStarDonateForCaster;

    public long totalMatchComplete;
    public long totalLeagueComplete;

    public Statistic(JsonObject data) {
        this.totalUserRegister = data.get("total_user_register").getAsLong();
        this.totalNewUsersThisWeek = data.get("total_new_user_this_week").getAsLong();
        this.totalUserDonate = data.get("total_user_donate").getAsLong();
        this.totalStarDonate = data.get("total_star_donate").getAsLong();
        this.totalStarDonateForMatch = data.get("total_star_donate_for_match").getAsLong();
        this.totalStarDonateForLeague = data.get("total_star_donate_for_league").getAsLong();
        this.totalStarDonateForGamer = data.get("total_star_donate_for_gamer").getAsLong();
        this.totalStarDonateForCaster = data.get("total_star_donate_for_caster").getAsLong();
        this.totalMatchComplete = data.get("total_match_complete").getAsLong();
        this.totalLeagueComplete = data.get("total_league_complete").getAsLong();
    }

    public void updateUserRegistrationCount(int newUserCount) {
        this.totalUserRegister += newUserCount;
    }

    public void updateNewUsersThisWeek(int newUsersCount) {
        this.totalNewUsersThisWeek += newUsersCount;

    }

    public void updateDonationStats(int donatedStars) {
        this.totalStarDonate += donatedStars;

    }

    public void updateTournamentStats(int tournaments) {
        this.totalStarDonateForLeague += tournaments;

    }

    public void updateMatchStats(int championships) {
        this.totalStarDonateForMatch += championships;

    }

    public void updateStarsDonatedToEntities(int entity, int stars) {
        switch (entity) {
            case ENTITY_GAMER:
                this.totalStarDonateForGamer += stars;
            case ENTITY_MATCH:
                this.totalStarDonateForMatch += stars;
            case ENTITY_CASTER:
                this.totalStarDonateForCaster += stars;
            case ENTITY_LEAGUE:
                this.totalStarDonateForLeague += stars;

        }

    }
}
