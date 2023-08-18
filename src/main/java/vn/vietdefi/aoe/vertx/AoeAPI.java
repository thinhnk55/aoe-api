package vn.vietdefi.aoe.vertx;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.aoe.vertx.router.caster.CasterRouter;
import vn.vietdefi.aoe.vertx.router.clan.ClanRouter;
import vn.vietdefi.aoe.vertx.router.gamer.GamerRouter;
import vn.vietdefi.aoe.vertx.router.match.MatchRouter;
import vn.vietdefi.aoe.vertx.router.profile.ProfileRouter;
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
    }

    private static void walletApi(Router router) {
        router.post(ApiConfig.instance().getPath("/wallet/get"))
                .handler(WalletRouter::getWallet);
        router.get(ApiConfig.instance().getPath("/wallet/list-recharge"))
                .handler(WalletRouter::listRecharge);
        router.get(ApiConfig.instance().getPath("/wallet/list-transaction"))
                .handler(WalletRouter::listTransaction);
        router.get(ApiConfig.instance().getPath("/wallet/transaction"))
                .handler(WalletRouter::getTransaction);
    }

    private static void authApi(Router router) {
        router.post(ApiConfig
                        .instance()
                        .getPath("/aoefan/register"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::register);
        router.post(ApiConfig
                        .instance()
                        .getPath("/aoefan/login"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::login);
    }
    private static void adminApi(Router router) {

    }

    private static void userApi(Router router) {

    }

    private static void matchApi(Router router) {
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/create"))
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
                        .getPath("/match/VotingMatch/stop"))
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
                        .getPath("/match/user/suggest"))
                .handler(BodyHandler.create(false))
                .handler(MatchRouter::createMatchSuggest);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/user/suggest/update"))
                .handler(BodyHandler.create(false))
                .handler(MatchRouter::updateMatchSuggest);
        router.post(ApiConfig
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
    }

    public static void profileApi(Router router) {
        router.post(ApiConfig.instance().getPath("/profile/get"))
                .handler(ProfileRouter::getProfile);
        router.post(ApiConfig.instance().getPath("/profile/update"))
                .handler(BodyHandler.create(false))
                .handler(ProfileRouter::updateProfile);
    }
}
