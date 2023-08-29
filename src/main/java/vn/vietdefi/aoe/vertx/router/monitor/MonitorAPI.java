package vn.vietdefi.aoe.vertx.router.monitor;

import io.vertx.ext.web.Router;
import vn.vietdefi.api.vertx.ApiConfig;

public class MonitorAPI {
    public static void configAPI(Router router){
        userAuthApi(router);
        supportAuthApi(router);
        adminAuthApi(router);
        superAdminAuthApi(router);
        systemAdminAuthApi(router);
    }

    public static void userAuthApi(Router router) {
    }
    public static void supportAuthApi(Router router) {

    }
    public static void adminAuthApi(Router router) {
        router.get(ApiConfig.instance().getPath("/monitor/get"))
                .handler(MonitorRouter::getMonitor);
    }
    public static void superAdminAuthApi(Router router) {

    }
    public static void systemAdminAuthApi(Router router) {

    }
}
