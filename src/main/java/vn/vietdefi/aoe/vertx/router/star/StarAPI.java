package vn.vietdefi.aoe.vertx.router.star;

import io.vertx.ext.web.Router;
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
        router.get(ApiConfig.instance().getPath("/star/list-by-service"))
                .handler(AuthRouter::authorizeUser)
                .handler(StarRouter::listByService);
        router.get(ApiConfig.instance().getPath("/star/list-transaction"))
                .handler(AuthRouter::authorizeUser)
                .handler(StarRouter::listTransaction);
        router.get(ApiConfig.instance().getPath("/star/transaction"))
                .handler(AuthRouter::authorizeUser)
                .handler(StarRouter::getTransaction);
        router.get(ApiConfig.instance().getPath("/star/transaction-by-time"))
                .handler(AuthRouter::authorizeUser)
                .handler(StarRouter::listByTime);
    }
    public static void supportAuthApi(Router router) {
        router.get(ApiConfig.instance().getPath("/star/admin/get"))
                .handler(AuthRouter::authorizeSupport)
                .handler(StarRouter::adminGetStarWallet);
    }
    public static void adminAuthApi(Router router) {

    }
    public static void superAdminAuthApi(Router router) {

    }
    public static void systemAdminAuthApi(Router router) {

    }
}
