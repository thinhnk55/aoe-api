package vn.vietdefi.aoe.vertx.router.event;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.aoe.vertx.router.gamer.GamerRouter;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class EventAPI {
    public static void configAPI(Router router) {
        userAuthApi(router);
        supportAuthApi(router);
        adminAuthApi(router);
        superAdminAuthApi(router);
        systemAdminAuthApi(router);
    }

    public static void userAuthApi(Router router) {
        router.get(ApiConfig.instance().getPath("/event/get"))
                .handler(EventRouter::getEvent);
        router.get(ApiConfig.instance().getPath("/event/list-participant"))
                .handler(EventRouter::getListParticipants);
        router.get(ApiConfig.instance().getPath("/event/list-by-state"))
                .handler(EventRouter::getListEventByState);
//        router.get(ApiConfig.instance().getPath("/event/get-by-match"))
//                .handler(EventRouter::getEventByMatch);
        router.post(ApiConfig.instance().getPath("/event/join"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeUser)
                .handler(EventRouter::joinEvent);
    }

    public static void supportAuthApi(Router router) {

    }

    public static void adminAuthApi(Router router) {
        router.post(ApiConfig.instance().getPath("/event/create"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(EventRouter::createEvent);
        router.post(ApiConfig.instance().getPath("/event/update-state"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(EventRouter::updateStateEvent);
        router.get(ApiConfig.instance().getPath("/event/list/winning"))
                .handler(AuthRouter::authorizeAdmin)
                .handler(EventRouter::getListWinning);
        router.post(ApiConfig.instance().getPath("/event/cancel"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(EventRouter::cancelParticipant);
        router.post(ApiConfig.instance().getPath("/event/award"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeAdmin)
                .handler(EventRouter::awardParticipant);
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
