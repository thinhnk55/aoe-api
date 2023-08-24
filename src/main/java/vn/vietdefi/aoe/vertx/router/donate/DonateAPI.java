package vn.vietdefi.aoe.vertx.router.donate;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class DonateAPI {
    public static void configAPI(Router router){
        userAuthApi(router);
        supportAuthApi(router);
        adminAuthApi(router);
        superAdminAuthApi(router);
        systemAdminAuthApi(router);
    }

    public static void userAuthApi(Router router) {
        router.post(ApiConfig.instance().getPath("/donate/gamer"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeUser)
                .handler(DonateRouter::donateGamer);
        router.post(ApiConfig.instance().getPath("/donate/caster"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeUser)
                .handler(DonateRouter::donateCaster);
        router.post(ApiConfig.instance().getPath("/donate/match"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeUser)
                .handler(DonateRouter::donateMatch);
        router.post(ApiConfig.instance().getPath("/donate/league"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeUser)
                .handler(DonateRouter::donateLeague);
        router.get(ApiConfig.instance().getPath("/donate/list"))
                .handler(AuthRouter::authorizeUser)
                .handler(DonateRouter::listDonate);
        router.get(ApiConfig.instance().getPath("/donate/list-top"))
                .handler(AuthRouter::authorizeUser)
                .handler(DonateRouter::listTopDonate);
        router.get(ApiConfig.instance().getPath("/donate/list-top-all"))
                .handler(AuthRouter::authorizeUser)
                .handler(DonateRouter::listAllTopDonate);
        router.get(ApiConfig.instance().getPath("/donate/list-of-user"))
                .handler(DonateRouter::listDonateOfUser);
        router.get(ApiConfig.instance().getPath("/donate/list-gamer-favorites"))
                .handler(DonateRouter::listGamerFavorites);
    }
    public static void supportAuthApi(Router router) {

    }
    public static void adminAuthApi(Router router) {

    }
    public static void superAdminAuthApi(Router router) {

    }
    public static void systemAdminAuthApi(Router router) {

    }
}
