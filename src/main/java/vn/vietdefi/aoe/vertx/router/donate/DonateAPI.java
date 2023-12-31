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
                .handler(DonateRouter::listDonate);
        router.get(ApiConfig.instance().getPath("/donate/list-top"))
                .handler(DonateRouter::listTopDonate);
        router.get(ApiConfig.instance().getPath("/donate/list-top-all"))
                .handler(DonateRouter::listAllTopDonate);
        router.get(ApiConfig.instance().getPath("/donate/list/filter"))
                .handler(DonateRouter::filterListDonate);
        router.get(ApiConfig.instance().getPath("/donate/statistic/filter"))
                .handler(DonateRouter::filterStatisticDonate);
        router.get(ApiConfig.instance().getPath("/donate/list/outstanding"))
                .handler(DonateRouter::listDonateOutstanding);
        router.get(ApiConfig.instance().getPath("/donate/statistic/user"))
                .handler(DonateRouter::statisticDonateByUserId);
        router.get(ApiConfig.instance().getPath("/donate/detail"))
                .handler(AuthRouter::authorizeUser)
                .handler(DonateRouter::detailDonateById);


    }
    public static void supportAuthApi(Router router) {
        router.get(ApiConfig.instance().getPath("/donate/statistic"))
                .handler(AuthRouter::authorizeSupport)
                .handler(DonateRouter::statisticsDonate);


    }
    public static void adminAuthApi(Router router) {

    }
    public static void superAdminAuthApi(Router router) {

    }
    public static void systemAdminAuthApi(Router router) {

    }
}
