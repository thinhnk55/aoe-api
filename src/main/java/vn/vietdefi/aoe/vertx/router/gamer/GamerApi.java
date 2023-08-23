package vn.vietdefi.aoe.vertx.router.gamer;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.aoe.vertx.router.clan.ClanRouter;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class GamerApi {
    public static void configAPI(Router router) {
        userAuthApi(router);
        supportAuthApi(router);
        adminAuthApi(router);
        superAdminAuthApi(router);
        systemAdminAuthApi(router);
    }

    public static void userAuthApi(Router router) {
        router.get(ApiConfig
                        .instance()
                        .getPath("/gamer/get"))
                .handler(GamerRouter::getGamerByUserId);

        router.get(ApiConfig.instance().getPath("/gamer/list"))
                .handler(GamerRouter::listGamer);
        router.get(ApiConfig.instance().getPath("/gamer/list-of-clan"))
                .handler(GamerRouter::listGamerOfClan);
    }

    public static void supportAuthApi(Router router) {
        router.post(ApiConfig
                        .instance()
                        .getPath("/gamer/create"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(GamerRouter::create);
        router.post(ApiConfig
                        .instance()
                        .getPath("/gamer/update"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(GamerRouter::updateInfo);

    }

    public static void adminAuthApi(Router router) {

    }

    public static void superAdminAuthApi(Router router) {

    }

    public static void systemAdminAuthApi(Router router) {
        router.post(ApiConfig.instance().getPath("/gamer/delete"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSystemAdmin)
                .handler(GamerRouter::deleteGamer);
    }
}
