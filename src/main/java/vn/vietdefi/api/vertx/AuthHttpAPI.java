package vn.vietdefi.api.vertx;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.api.router.auth.AuthRouter;

public class AuthHttpAPI {
    public static Vertx vertx;
    public static void configAPI(Router router) {
        authApi(router);
    }
    private static void authApi(Router router) {
        router.post(ApiGameConfig
                        .instance()
                        .getPath("/register"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::register);
        router.post(ApiGameConfig
                        .instance()
                        .getPath("/login"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::login);
    }
}
