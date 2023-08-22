package vn.vietdefi.aoe.vertx;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.aoe.vertx.router.auth.AoeAuthAPI;
import vn.vietdefi.aoe.vertx.router.caster.CasterAPI;
import vn.vietdefi.aoe.vertx.router.caster.CasterRouter;
import vn.vietdefi.aoe.vertx.router.clan.ClanAPI;
import vn.vietdefi.aoe.vertx.router.donate.DonateAPI;
import vn.vietdefi.aoe.vertx.router.donate.DonateRouter;
import vn.vietdefi.aoe.vertx.router.event.EventRouter;
import vn.vietdefi.aoe.vertx.router.gamer.GamerApi;
import vn.vietdefi.aoe.vertx.router.gamer.GamerRouter;
import vn.vietdefi.aoe.vertx.router.match.MatchAPI;
import vn.vietdefi.aoe.vertx.router.match.MatchRouter;
import vn.vietdefi.aoe.vertx.router.profile.ProfileAPI;
import vn.vietdefi.aoe.vertx.router.star.StarAPI;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

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
        adminApi(router);
        eventApi(router);
    }

    private static void eventApi(Router router) {
        router.post(ApiConfig.instance().getPath("/event/create"))
                .handler(BodyHandler.create())
                .handler(EventRouter::createEvent);
        router.post(ApiConfig.instance().getPath("/event/lock"))
                .handler(BodyHandler.create())
//                .handler(AuthRouter::authorizeAdmin)
                .handler(EventRouter::lockEvent);
        router.get(ApiConfig.instance().getPath("/event/info"))
                .handler(EventRouter::getEvent);
        router.post(ApiConfig.instance().getPath("/event/join"))
                .handler(BodyHandler.create())
                .handler(AuthRouter::authorizeUser)
                .handler(EventRouter::addParticipant);
        router.get(ApiConfig.instance().getPath("/event/list/participants"))
                .handler(EventRouter::getListParticipants);
        router.get(ApiConfig.instance().getPath("/event/list"))
                .handler(EventRouter::getEventByState);
        router.get(ApiConfig.instance().getPath("/event/list/winning"))
                .handler(EventRouter::getListWinning);
        router.get(ApiConfig.instance().getPath("/event/bymatch"))
                .handler(EventRouter::getEventByMatch);
        router.get(ApiConfig.instance().getPath("/event/history/participant"))
                .handler(AuthRouter::authorizeUser)
                .handler(EventRouter::getListHistoryParticipant);
    }

    private static void adminApi(Router router) {

    }

}
