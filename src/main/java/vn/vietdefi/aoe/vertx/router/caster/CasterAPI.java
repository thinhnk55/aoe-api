package vn.vietdefi.aoe.vertx.router.caster;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class CasterAPI {
    public static void configAPI(Router router){
        userAuthApi(router);
        supportAuthApi(router);
        adminAuthApi(router);
        superAdminAuthApi(router);
        systemAdminAuthApi(router);
    }

    public static void userAuthApi(Router router) {
        router.get(ApiConfig.instance().getPath("/caster/get"))
                .handler(CasterRouter::getCasterByUserId);
        router.get(ApiConfig.instance().getPath("/caster/list"))
                .handler(CasterRouter::listCaster);
    }
    public static void supportAuthApi(Router router) {

    }
    public static void adminAuthApi(Router router) {
        router.post(ApiConfig.instance().getPath("/caster/create"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(CasterRouter::createCaster);
        router.post(ApiConfig.instance().getPath("/caster/update"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(CasterRouter::updateCaster);
    }
    public static void superAdminAuthApi(Router router) {

    }
    public static void systemAdminAuthApi(Router router) {
        router.post(ApiConfig.instance().getPath("/caster/delete"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSystemAdmin)
                .handler(CasterRouter::deleteCaster);
    }
}
