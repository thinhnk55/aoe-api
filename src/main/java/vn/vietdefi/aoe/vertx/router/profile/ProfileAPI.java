package vn.vietdefi.aoe.vertx.router.profile;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class ProfileAPI {
    public static void configAPI(Router router){
        userAuthApi(router);
        supportAuthApi(router);
        adminAuthApi(router);
        superAdminAuthApi(router);
        systemAdminAuthApi(router);
    }

    public static void userAuthApi(Router router) {
        router.get(ApiConfig.instance().getPath("/profile/get"))
                .handler(AuthRouter::authorizeUser)
                .handler(ProfileRouter::getProfile);
        router.post(ApiConfig.instance().getPath("/profile/update"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeUser)
                .handler(ProfileRouter::updateProfile);
        router.post(ApiConfig.instance().getPath("/profile/update/lang"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeUser)
                .handler(ProfileRouter::updateLanguage);
        router.get(ApiConfig.instance().getPath("/profile/outstanding-view"))
                .handler(ProfileRouter::outstandingView);
        router.get(ApiConfig.instance().getPath("/profile/list-gamer-favorites"))
                .handler(ProfileRouter::listGamerFavorites);
        router.get(ApiConfig.instance().getPath("/profile/get-partial"))
                .handler(ProfileRouter::getPartialProfile);
    }
    public static void supportAuthApi(Router router) {
        router.get(ApiConfig.instance().getPath("/profile/search"))
                .handler(AuthRouter::authorizeSupport)
                .handler(ProfileRouter::searchProfile);
    }
    public static void adminAuthApi(Router router) {

    }
    public static void superAdminAuthApi(Router router) {

    }
    public static void systemAdminAuthApi(Router router) {

    }
}
