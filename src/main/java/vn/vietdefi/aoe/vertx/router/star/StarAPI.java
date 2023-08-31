package vn.vietdefi.aoe.vertx.router.star;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.aoe.vertx.router.donate.DonateRouter;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class StarAPI {
    public static void configAPI(Router router){
        userAuthApi(router);
        supportAuthApi(router);
        adminAuthApi(router);
        superAdminAuthApi(router);
        systemAdminAuthApi(router);
    }

    public static void userAuthApi(Router router) {
        router.get(ApiConfig.instance().getPath("/star/get"))
                .handler(AuthRouter::authorizeUser)
                .handler(StarRouter::getStarWallet);

        router.get(ApiConfig.instance().getPath("/star/transaction/service"))
                .handler(AuthRouter::authorizeUser)
                .handler(StarRouter::listByService);

        router.get(ApiConfig.instance().getPath("/star/transaction/get"))
                //.handler(AuthRouter::authorizeUser)
                .handler(StarRouter::getTransaction);

        router.get(ApiConfig.instance().getPath("/star/transaction/time"))
                .handler(AuthRouter::authorizeUser)
                .handler(StarRouter::listByTime);
    }
    public static void supportAuthApi(Router router) {
        router.get(ApiConfig.instance().getPath("/star/admin/get"))
                .handler(AuthRouter::authorizeSupport)
                .handler(StarRouter::adminGetStarWallet);
        router.get(ApiConfig.instance().getPath("/star/admin/transaction/recharge"))
                .handler(AuthRouter::authorizeSupport)
                .handler(StarRouter::lookupRechargeHistory);
        router.get(ApiConfig.instance().getPath("/star/admin/transaction/time"))
                .handler(AuthRouter::authorizeSupport)
                .handler(StarRouter::adminListOfUserByTime);
        router.get(ApiConfig.instance().getPath("/star/refund/list"))
                .handler(AuthRouter::authorizeSupport)
                .handler(StarRouter::filterListRefund);
        router.get(ApiConfig.instance().getPath("/star/statistic"))
                .handler(AuthRouter::authorizeSupport)
                .handler(StarRouter::getStatisticRecharge);
    }
    public static void adminAuthApi(Router router) {

    }
    public static void superAdminAuthApi(Router router) {

    }
    public static void systemAdminAuthApi(Router router) {
        router.post(ApiConfig.instance().getPath("/star/admin/exchange"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSystemAdmin)
                .handler(StarRouter::systemAdminExchangeStar);
    }
}
