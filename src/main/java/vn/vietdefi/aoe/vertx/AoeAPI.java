package vn.vietdefi.aoe.vertx;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.aoe.vertx.router.auth.AoeAuthAPI;
import vn.vietdefi.aoe.vertx.router.caster.CasterAPI;
import vn.vietdefi.aoe.vertx.router.caster.CasterRouter;
import vn.vietdefi.aoe.vertx.router.clan.ClanAPI;
import vn.vietdefi.aoe.vertx.router.donate.DonateRouter;
import vn.vietdefi.aoe.vertx.router.event.EventRouter;
import vn.vietdefi.aoe.vertx.router.gamer.GamerApi;
import vn.vietdefi.aoe.vertx.router.gamer.GamerRouter;
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
        adminApi(router);
        matchApi(router);
        donateApi(router);
        eventApi(router);
    }

    private static void donateApi(Router router) {
        router.post(ApiConfig.instance().getPath("/donate/gamer"))
                .handler(BodyHandler.create(false))
                .handler(DonateRouter::donateGamer);
        router.post(ApiConfig.instance().getPath("/donate/caster"))
                .handler(BodyHandler.create(false))
                .handler(DonateRouter::donateCaster);
        router.post(ApiConfig.instance().getPath("/donate/match"))
                .handler(BodyHandler.create(false))
                .handler(DonateRouter::donateMatch);
        router.get(ApiConfig.instance().getPath("/donate/list-donate"))
                .handler(DonateRouter::listDonate);
        router.get(ApiConfig.instance().getPath("/donate/list-top-donate"))
                .handler(DonateRouter::listTopDonate);
        router.get(ApiConfig.instance().getPath("/donate/list-all-top-donate"))
                .handler(DonateRouter::listAllTopDonate);
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


    private static void matchApi(Router router) {
        router.post(ApiConfig
                        .instance()
                        .getPath("/admin/match/create"))
                .handler(BodyHandler.create(false))
                .handler(MatchRouter::create);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/update"))
                .handler(BodyHandler.create(false))
                .handler(MatchRouter::update);
        router.get(ApiConfig
                        .instance()
                        .getPath("/match/get/byId"))
                .handler(MatchRouter::getById);
        router.get(ApiConfig
                        .instance()
                        .getPath("/match/getList/state"))
                .handler(MatchRouter::getListMatch);
        router.get(ApiConfig
                        .instance()
                        .getPath("/match/outstanding"))
                .handler(MatchRouter::getOutstandingMatch);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/voting/stop"))
                .handler(BodyHandler.create(false))
                .handler(MatchRouter::StopVotingMatch);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/start"))
                .handler(BodyHandler.create(false))
                .handler(MatchRouter::startMatch);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/end"))
                .handler(BodyHandler.create(false))
                .handler(MatchRouter::endMatch);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/update/result"))
                .handler(BodyHandler.create(false))
                .handler(MatchRouter::updateResult);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/cancel"))
                .handler(BodyHandler.create(false))
                .handler(MatchRouter::cancelMatch);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/confirm"))
                .handler(BodyHandler.create(false))
                .handler(MatchRouter::confirmMatch);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/suggest/cancel"))
                .handler(BodyHandler.create(false))
                .handler(MatchRouter::cancelMatchSuggest);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/user/suggest"))
                .handler(BodyHandler.create(false))
                .handler(MatchRouter::createMatchSuggest);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/user/suggest/update"))
                .handler(BodyHandler.create(false))
                .handler(MatchRouter::updateMatchSuggest);
        router.get(ApiConfig
                        .instance()
                        .getPath("/match/user/suggest/list"))
                .handler(MatchRouter::getListMatchSuggested);
    }
}
