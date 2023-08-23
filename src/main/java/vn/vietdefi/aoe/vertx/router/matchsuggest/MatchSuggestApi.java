package vn.vietdefi.aoe.vertx.router.matchsuggest;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.aoe.vertx.router.match.MatchRouter;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class MatchSuggestApi {
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
                        .getPath("/match/user/suggest"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeUser)
                .handler(MatchSuggestRouter::createMatchSuggest);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/user/suggest/update"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeUser)
                .handler(MatchSuggestRouter::updateMatchSuggest);
        router.get(ApiConfig
                        .instance()
                        .getPath("/match/user/suggest/list"))
                .handler(AuthRouter::authorizeUser)
                .handler(MatchSuggestRouter::getListMatchSuggested);
        router.get(ApiConfig
                        .instance()
                        .getPath("/match/user/suggest/info"))
                .handler(AuthRouter::authorizeUser)
                .handler(MatchSuggestRouter::getMatchSuggestInfo);
    }
    public static void supportAuthApi(Router router) {

    }
    public static void adminAuthApi(Router router) {
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/confirm"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(MatchSuggestRouter::confirmMatch);
        router.post(ApiConfig
                        .instance()
                        .getPath("/match/suggest/cancel"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(MatchSuggestRouter::cancelMatchSuggest);
    }
    public static void superAdminAuthApi(Router router) {

    }
    public static void systemAdminAuthApi(Router router) {

    }
}
