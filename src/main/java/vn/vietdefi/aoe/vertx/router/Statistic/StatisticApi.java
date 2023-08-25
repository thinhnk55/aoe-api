package vn.vietdefi.aoe.vertx.router.Statistic;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.aoe.vertx.router.auth.AoeAuthRouter;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class StatisticApi {
    public static void configAPI(Router router){
        userAuthApi(router);
        supportAuthApi(router);
        adminAuthApi(router);
        superAdminAuthApi(router);
        systemAdminAuthApi(router);
    }

    public static void userAuthApi(Router router) {
        router.get(ApiConfig
                        .instance()
                        .getPath("/statistic/get"))
                .handler(StatisticRouter::getStatistic);
    }
    public static void supportAuthApi(Router router) {

    }
    public static void adminAuthApi(Router router) {
        router.get(ApiConfig
                        .instance()
                        .getPath("/statistic/get/full"))
                .handler(AuthRouter::authorizeAdmin)
                .handler(StatisticRouter::getAllStatistic);

    }
    public static void superAdminAuthApi(Router router) {

    }
    public static void systemAdminAuthApi(Router router) {
        router.post(ApiConfig
                        .instance()
                        .getPath("/statistic/update"))
                .handler(AuthRouter::authorizeSystemAdmin)
                .handler(StatisticRouter::adminCallBackUpdateStatistic);

    }
}
