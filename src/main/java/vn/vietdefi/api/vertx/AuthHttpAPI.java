package vn.vietdefi.api.vertx;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class AuthHttpAPI {
    public static Vertx vertx;
    public static void configAPI(Router router) {
        authApi(router);
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
}
