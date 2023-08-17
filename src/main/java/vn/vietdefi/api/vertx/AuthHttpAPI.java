package vn.vietdefi.api.vertx;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.api.router.AuthRouter;
import vn.vietdefi.api.router.BankRouter;

public class AuthHttpAPI {
    public static Vertx vertx;
    public static void configAPI(Router router) {
        authApi(router);
        bankApi(router);
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
    private static void bankApi(Router router){
        router.post(ApiGameConfig.instance().getPath("/bank/login"))
                .handler(BodyHandler.create(false))
                .handler(BankRouter::login);
        router.post(ApiGameConfig.instance().getPath("/bank/commit"))
                .handler(BodyHandler.create(false))
                .handler(BankRouter::commit);
    }
}
