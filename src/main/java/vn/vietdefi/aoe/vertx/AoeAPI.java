package vn.vietdefi.aoe.vertx;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.aoe.vertx.router.caster.CasterRouter;
import vn.vietdefi.aoe.vertx.router.clan.ClanRouter;
import vn.vietdefi.aoe.vertx.router.donate.DonateRouter;
import vn.vietdefi.aoe.vertx.router.event.EventRouter;
import vn.vietdefi.aoe.vertx.router.gamer.GamerRouter;
import vn.vietdefi.aoe.vertx.router.match.MatchRouter;
import vn.vietdefi.aoe.vertx.router.profile.ProfileRouter;
import vn.vietdefi.aoe.vertx.router.user.UserRouter;
import vn.vietdefi.aoe.vertx.router.wallet.WalletRouter;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class AoeAPI {
    public static void configAPI(Router router) {
        authApi(router);
        userApi(router);
        adminApi(router);
        gamerApi(router);
        casterAPI(router);
        profileApi(router);
        walletApi(router);
        clanApi(router);
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
        router.get(ApiConfig.instance().getPath("/donate/list-fan-donate"))
                .handler(DonateRouter::listFanDonate);
        router.get(ApiConfig.instance().getPath("/donate/list-top-donate"))
                .handler(DonateRouter::listTopDonate);
        router.get(ApiConfig.instance().getPath("/donate/list-top-donate-time"))
                .handler(DonateRouter::listTopDonateByTime);
        router.get(ApiConfig.instance().getPath("/match/user/donate"))
                .handler(DonateRouter::listTopByTime);
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

    private static void walletApi(Router router) {
        router.get(ApiConfig.instance().getPath("/wallet/get"))
                .handler(WalletRouter::getWallet);
        router.get(ApiConfig.instance().getPath("/wallet/list-by-service"))
                .handler(WalletRouter::listByService);
        router.get(ApiConfig.instance().getPath("/wallet/list-transaction"))
                .handler(WalletRouter::listTransaction);
        router.get(ApiConfig.instance().getPath("/wallet/transaction"))
                .handler(WalletRouter::getTransaction);
        router.get(ApiConfig.instance().getPath("/wallet/get-user"))
                .handler(WalletRouter::getUserWallet);
    }

    private static void authApi(Router router) {
        router.post(ApiConfig
                        .instance()
                        .getPath("/auth/register"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::register);
        router.post(ApiConfig
                        .instance()
                        .getPath("/auth/login"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::login);
        router.post(ApiConfig.instance().getPath("/auth/logout"))
                .handler(AuthRouter::logout);
    }
    private static void adminApi(Router router) {

    }

    private static void userApi(Router router) {
        router.post(ApiConfig.instance().getPath("/user/lock"))
                .handler(UserRouter::lockUser);
        router.post(ApiConfig.instance().getPath("/user/unlock"))
                .handler(UserRouter::unLockUser);
        router.post(ApiConfig.instance().getPath("/user/change-password"))
                .handler(BodyHandler.create(false))
                .handler(UserRouter::changePassword);
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
    public static void gamerApi(Router router){
        router.post(ApiConfig
                        .instance()
                        .getPath("/gamer/create"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(GamerRouter::create);
        router.post(ApiConfig
                        .instance()
                        .getPath("/gamer/update"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(GamerRouter::updateInfo);
        router.get(ApiConfig
                        .instance()
                        .getPath("/gamer/get"))
                .handler(GamerRouter::getGamerByUserId);
        router.get(ApiConfig.instance().getPath("/gamer/list-of-match"))
                .handler(GamerRouter::listGamerByMatchId);
        router.get(ApiConfig.instance().getPath("/gamer/list"))
                .handler(GamerRouter::listGamer);
        router.get(ApiConfig.instance().getPath("/gamer/list-of-clan"))
                .handler(GamerRouter::listGamerOfClan);
        router.get(ApiConfig.instance().getPath("/gamer/match"))
                .handler(GamerRouter::listMatch);
    }

    public static void casterAPI(Router router) {
        router.post(ApiConfig
                        .instance()
                        .getPath("/caster/create"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(CasterRouter::createCaster);
        router.post(ApiConfig
                        .instance()
                        .getPath("/caster/update"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(CasterRouter::updateCaster);
        router.get(ApiConfig
                        .instance()
                        .getPath("/caster/get"))
                .handler(CasterRouter::getCasterByUserId);
    }
    public static void clanApi(Router router){
        router.post(ApiConfig.instance().getPath("/clan/get"))
                .handler(ClanRouter::getInfoClan);
        router.post(ApiConfig.instance().getPath("/clan/create"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(ClanRouter::createClan);
        router.post(ApiConfig.instance().getPath("/clan/update"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(ClanRouter::updateClan);
        router.get(ApiConfig.instance().getPath("/clan/list"))
                .handler(ClanRouter::getListClan);
    }

    public static void profileApi(Router router) {
        router.post(ApiConfig.instance().getPath("/profile/get"))
                .handler(ProfileRouter::getProfile);
        router.post(ApiConfig.instance().getPath("/profile/update"))
                .handler(BodyHandler.create(false))
                .handler(ProfileRouter::updateProfile);
        router.post(ApiConfig.instance().getPath("/profile/search"))
                .handler(BodyHandler.create(false))
                .handler(ProfileRouter::searchProfile);
        router.post(ApiConfig.instance().getPath("/profile/update-language"))
                .handler(BodyHandler.create(false))
                .handler(ProfileRouter::updateLanguage);
    }
}
