package vn.vietdefi.aoe.vertx.router.event;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.aoe.vertx.router.match.MatchRouter;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class EventAPI {
    public static void configAPI(Router router){
        userAuthApi(router);
        supportAuthApi(router);
        adminAuthApi(router);
        superAdminAuthApi(router);
        systemAdminAuthApi(router);
    }

    public static void userAuthApi(Router router) {
        router.post(ApiConfig.instance().getPath("/event/create"))
                .handler(BodyHandler.create())
                .handler(EventRouter::createEvent);
        router.post(ApiConfig.instance().getPath("/event/lock"))
                .handler(BodyHandler.create())
//                .handler(AuthRouter::authorizeAdmin)
                .handler(EventRouter::lockEvent);
        router.get(ApiConfig.instance().getPath("/event/info"))
                .handler(EventRouter::getEvent);
        router.post(ApiConfig.instance().getPath("/event/join"))
                .handler(BodyHandler.create())
                .handler(AuthRouter::authorizeUser)
                .handler(EventRouter::addParticipant);
        router.get(ApiConfig.instance().getPath("/event/list/participants"))
                .handler(EventRouter::getListParticipants);
        router.get(ApiConfig.instance().getPath("/event/list"))
                .handler(EventRouter::getEventByState);
        router.get(ApiConfig.instance().getPath("/event/list/winning"))
                .handler(EventRouter::getListWinning);
        router.get(ApiConfig.instance().getPath("/event/bymatch"))
                .handler(EventRouter::getEventByMatch);
        router.get(ApiConfig.instance().getPath("/event/history/participant"))
                .handler(AuthRouter::authorizeUser)
                .handler(EventRouter::getListHistoryParticipant);
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
                        .getPath("/admin/event/delete"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSystemAdmin);
    }
}
