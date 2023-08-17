package vn.vietdefi.aoe.vertx;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.aoe.router.caster.CasterRouter;
import vn.vietdefi.aoe.router.gamer.GamerRouter;
import vn.vietdefi.api.router.AuthRouter;
import vn.vietdefi.api.vertx.ApiGameConfig;

public class AoeAPI {
    public static void configAPI(Router router) {
        userApi(router);
        adminApi(router);
        gamerApi(router);
        casterAPI(router);
    }

    private static void adminApi(Router router) {

    }

    private static void userApi(Router router) {

    }
    public static void gamerApi(Router router){
        router.post(ApiGameConfig
                        .instance()
                        .getPath("/gamer/create"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(GamerRouter::create);
        router.post(ApiGameConfig
                        .instance()
                        .getPath("/gamer/update-info"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(GamerRouter::updateInfo);
        router.post(ApiGameConfig
                        .instance()
                        .getPath("/gamer/delete-accountant"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(GamerRouter::deleteAccountant);
    }
    public static void casterAPI(Router router){
        router.post(ApiGameConfig
                        .instance()
                        .getPath("/caster/create"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(CasterRouter::createCaster);
        router.post(ApiGameConfig
                        .instance()
                        .getPath("/caster/update"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(CasterRouter::updateCaster);
        router.post(ApiGameConfig
                        .instance()
                        .getPath("/caster/delete"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(CasterRouter::deleteCaster);
    }
}
