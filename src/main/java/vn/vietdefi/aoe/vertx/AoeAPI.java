package vn.vietdefi.aoe.vertx;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.aoe.vertx.router.caster.CasterRouter;
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
    }
    public static void casterAPI(Router router){
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
        router.post(ApiConfig
                        .instance()
                        .getPath("/caster/delete"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(CasterRouter::deleteCaster);
    }
    public static void profileApi(Router router){
        router.post(ApiConfig.instance().getPath("/profile/get"))
                .handler(ProfileRouter::getProfile);
        router.post(ApiConfig.instance().getPath("/profile/update"))
                .handler(BodyHandler.create(false))
                .handler(ProfileRouter::updateProfile);
    }
}
