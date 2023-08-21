package vn.vietdefi.aoe.vertx.router.auth;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class AoeAuthAPI {
    public static void configAPI(Router router){
        userAuthApi(router);
        supportAuthApi(router);
        adminAuthApi(router);
        superAdminAuthApi(router);
        systemAdminAuthApi(router);
    }

    public static void userAuthApi(Router router) {
        router.post(ApiConfig
                        .instance()
                        .getPath("/auth/register"))
                .handler(BodyHandler.create(false))
                .handler(AoeAuthRouter::register);
        router.post(ApiConfig
                        .instance()
                        .getPath("/auth/login"))
                .handler(BodyHandler.create(false))
                .handler(AoeAuthRouter::login);
        router.post(ApiConfig.instance().getPath("/auth/logout"))
                .handler(AuthRouter::authorizeUser)
                .handler(AuthRouter::logout);
    }
    public static void supportAuthApi(Router router) {

    }
    public static void adminAuthApi(Router router) {

    }
    public static void superAdminAuthApi(Router router) {

    }
    public static void systemAdminAuthApi(Router router) {
        router.post(ApiConfig
                        .instance()
                        .getPath("/auth/delete_user"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSystemAdmin)
                .handler(AoeAuthRouter::deleteUser);
        router.post(ApiConfig
                        .instance()
                        .getPath("/auth/update_user_id"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSystemAdmin)
                .handler(AuthRouter::updateUserId);
        router.post(ApiConfig
                        .instance()
                        .getPath("/auth/update_user_name"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSystemAdmin)
                .handler(AuthRouter::updateUsername);
        // only login for gamer test
        router.post(ApiConfig
                        .instance()
                        .getPath("/auth/login/gamer"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSystemAdmin)
                .handler(AoeAuthRouter::loginGamer);
    }
}
