package vn.vietdefi.aoe.vertx.router.donor;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class DonorAPI {
    public static void configAPI(Router router){
        userAuthApi(router);
        supportAuthApi(router);
        adminAuthApi(router);
        superAdminAuthApi(router);
        systemAdminAuthApi(router);
    }

    public static void userAuthApi(Router router) {
        router.get(ApiConfig.instance().getPath("/donor/get"))
                .handler(DonorRouter::getDonor);
        router.get(ApiConfig.instance().getPath("/donor/list"))
                .handler(DonorRouter::getListDonor);
    }
    public static void supportAuthApi(Router router) {

    }
    public static void adminAuthApi(Router router) {
        router.post(ApiConfig.instance().getPath("/donor/create"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(DonorRouter::createDonor);
        router.post(ApiConfig.instance().getPath("/donor/update"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(DonorRouter::updateDonor);
    }
    public static void superAdminAuthApi(Router router) {

    }
    public static void systemAdminAuthApi(Router router) {
        router.post(ApiConfig.instance().getPath("/donor/delete"))
                .handler(AuthRouter::authorizeSystemAdmin)
                .handler(DonorRouter::deleteDonor);
    }
}
