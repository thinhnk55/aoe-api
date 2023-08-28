package vn.vietdefi.aoe.vertx.router.league;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.aoe.vertx.router.match.MatchRouter;
import vn.vietdefi.aoe.vertx.router.profile.ProfileRouter;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class LeagueAPI {
    public static void configAPI(Router router){
        userAuthApi(router);
        supportAuthApi(router);
        adminAuthApi(router);
        superAdminAuthApi(router);
        systemAdminAuthApi(router);
    }

    public static void userAuthApi(Router router) {
        router.get(ApiConfig
                        .instance()
                        .getPath("/league/get"))
                .handler(BodyHandler.create(false))
                .handler(LeagueRouter::getInfo);
        router.get(ApiConfig
                        .instance()
                        .getPath("/league/list"))
                .handler(BodyHandler.create(false))
                .handler(LeagueRouter::getListByState);
    }
    public static void supportAuthApi(Router router) {

    }
    public static void adminAuthApi(Router router) {
        router.post(ApiConfig
                        .instance()
                        .getPath("/league/create"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(LeagueRouter::create);
        router.post(ApiConfig
                        .instance()
                        .getPath("/league/update"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(LeagueRouter::update);
        router.post(ApiConfig
                        .instance()
                        .getPath("/league/stop-vote"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(LeagueRouter::stopVoteLeague);
        router.post(ApiConfig
                        .instance()
                        .getPath("/league/start"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(LeagueRouter::startLeague);
        router.post(ApiConfig
                        .instance()
                        .getPath("/league/end"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(LeagueRouter::endLeague);
        router.post(ApiConfig
                        .instance()
                        .getPath("/league/cancel"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(LeagueRouter::cancelLeague);
    }
    public static void superAdminAuthApi(Router router) {

    }
    public static void systemAdminAuthApi(Router router) {
        router.post(ApiConfig
                        .instance()
                        .getPath("/league/delete"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSystemAdmin)
                .handler(LeagueRouter::deleteLeague);
    }
}
