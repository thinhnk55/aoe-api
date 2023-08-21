package vn.vietdefi.aoe.vertx.router.clan;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.aoe.vertx.router.profile.ProfileRouter;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class ClanAPI {
    public static void configAPI(Router router){
        userAuthApi(router);
        supportAuthApi(router);
        adminAuthApi(router);
        superAdminAuthApi(router);
        systemAdminAuthApi(router);
    }

    public static void userAuthApi(Router router) {
        router.get(ApiConfig.instance().getPath("/clan/get"))
                .handler(ClanRouter::getInfoClan);
        router.get(ApiConfig.instance().getPath("/clan/get-by-name"))
                .handler(ClanRouter::getInfoClanByName);
        router.get(ApiConfig.instance().getPath("/clan/list"))
                .handler(ClanRouter::getListClan);
    }
    public static void supportAuthApi(Router router) {

    }
    public static void adminAuthApi(Router router) {
        router.post(ApiConfig.instance().getPath("/clan/create"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(ClanRouter::createClan);
        router.post(ApiConfig.instance().getPath("/clan/update"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(ClanRouter::updateClan);
    }
    public static void superAdminAuthApi(Router router) {

    }
    public static void systemAdminAuthApi(Router router) {
        router.post(ApiConfig.instance().getPath("/clan/create"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSystemAdmin)
                .handler(ClanRouter::deleteClan);
    }
}
