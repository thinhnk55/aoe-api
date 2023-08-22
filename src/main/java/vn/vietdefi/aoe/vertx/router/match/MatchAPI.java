package vn.vietdefi.aoe.vertx.router.match;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.aoe.vertx.router.donate.DonateRouter;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class MatchAPI {
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
                        .getPath("/match/get/byId"))
                .handler(AuthRouter::authorizeUser)
                .handler(MatchRouter::getById);
        router.get(ApiConfig
                        .instance()
                        .getPath("/match/getList/state"))
                .handler(AuthRouter::authorizeUser)
                .handler(MatchRouter::getListMatch);
        router.get(ApiConfig
                        .instance()
                        .getPath("/match/outstanding"))
                .handler(AuthRouter::authorizeUser)
                .handler(MatchRouter::getOutstandingMatch);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/user/suggest"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeUser)
                .handler(MatchRouter::createMatchSuggest);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/user/suggest/update"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeUser)
                .handler(MatchRouter::updateMatchSuggest);
        router.get(ApiConfig
                        .instance()
                        .getPath("/match/user/suggest/list"))
                .handler(AuthRouter::authorizeUser)
                .handler(MatchRouter::getListMatchSuggested);
    }
    public static void supportAuthApi(Router router) {

    }
    public static void adminAuthApi(Router router) {
        router.post(ApiConfig
                        .instance()
                        .getPath("/admin/match/create"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(MatchRouter::create);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/update"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(MatchRouter::update);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/voting/stop"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(MatchRouter::StopVotingMatch);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/start"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(MatchRouter::startMatch);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/end"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(MatchRouter::endMatch);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/update/result"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(MatchRouter::updateResult);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/cancel"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(MatchRouter::cancelMatch);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/confirm"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(MatchRouter::confirmMatch);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/suggest/cancel"))
                .handler(BodyHandler.create(false))
                .handler(MatchRouter::cancelMatchSuggest);
    }
    public static void superAdminAuthApi(Router router) {

    }
    public static void systemAdminAuthApi(Router router) {

    }
}
