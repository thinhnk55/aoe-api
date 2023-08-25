package vn.vietdefi.aoe.vertx;

import io.vertx.ext.web.Router;
import vn.vietdefi.aoe.vertx.router.Statistic.StatisticApi;
import vn.vietdefi.aoe.vertx.router.auth.AoeAuthAPI;
import vn.vietdefi.aoe.vertx.router.caster.CasterAPI;
import vn.vietdefi.aoe.vertx.router.clan.ClanAPI;
import vn.vietdefi.aoe.vertx.router.donate.DonateAPI;
import vn.vietdefi.aoe.vertx.router.event.EventAPI;
import vn.vietdefi.aoe.vertx.router.gamer.GamerApi;
import vn.vietdefi.aoe.vertx.router.league.LeagueAPI;
import vn.vietdefi.aoe.vertx.router.match.MatchAPI;
import vn.vietdefi.aoe.vertx.router.matchsuggest.MatchSuggestApi;
import vn.vietdefi.aoe.vertx.router.profile.ProfileAPI;
import vn.vietdefi.aoe.vertx.router.star.StarAPI;

public class AoeAPI {
    public static void configAPI(Router router) {
        AoeAuthAPI.configAPI(router);
        ProfileAPI.configAPI(router);
        StarAPI.configAPI(router);
        ClanAPI.configAPI(router);
        GamerApi.configAPI(router);
        CasterAPI.configAPI(router);
        DonateAPI.configAPI(router);
        MatchAPI.configAPI(router);
        MatchSuggestApi.configAPI(router);
        EventAPI.configAPI(router);
        LeagueAPI.configAPI(router);
        adminApi(router);
        StatisticApi.configAPI(router);
    }
    private static void adminApi(Router router) {

    }

}
