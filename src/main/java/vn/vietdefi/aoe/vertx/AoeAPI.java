package vn.vietdefi.aoe.vertx;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.aoe.vertx.router.caster.CasterRouter;
import vn.vietdefi.aoe.vertx.router.gamer.GamerRouter;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class AoeAPI {
    public static void configAPI(Router router) {
        authApi(router);
        userApi(router);
        adminApi(router);
        gamerApi(router);
        casterAPI(router);
    }

    private static void authApi(Router router) {
        router.post(ApiConfig
                        .instance()
                        .getPath("/register"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::register);
        router.post(ApiConfig
                        .instance()
                        .getPath("/login"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::login);
    }
    private static void adminApi(Router router) {

    }

    private static void userApi(Router router) {

    }
    public static void gamerApi(Router router){
        router.post(ApiConfig
                        .instance()
                        .getPath("/gamer/create"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(GamerRouter::create);
        router.post(ApiConfig
                        .instance()
                        .getPath("/gamer/update-info"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(GamerRouter::updateInfo);
        router.post(ApiConfig
                        .instance()
                        .getPath("/gamer/delete-accountant"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(GamerRouter::deleteAccountant);
    }
    public static void casterAPI(Router router){
        router.post(ApiConfig
                        .instance()
                        .getPath("/caster/create"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(CasterRouter::createCaster);
        router.post(ApiConfig
                        .instance()
                        .getPath("/caster/update"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(CasterRouter::updateCaster);
        router.post(ApiConfig
                        .instance()
                        .getPath("/caster/delete"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(CasterRouter::deleteCaster);
    }
}
