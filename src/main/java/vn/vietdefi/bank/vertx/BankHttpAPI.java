package vn.vietdefi.bank.vertx;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class BankHttpAPI {
    public static void configAPI(Router router) {
        publicApi(router);
        userAuthApi(router);
        supportAuthApi(router);
        adminAuthApi(router);
        superAdminAuthApi(router);
        systemAdminAuthApi(router);
    }
    private static void publicApi(Router router){
        router.get(ApiConfig.instance().getPath("/bank/get/work"))
                .handler(BodyHandler.create(false))
                .handler(BankRouter::getWorkingBank);
    }
    public static void userAuthApi(Router router) {

    }
    public static void supportAuthApi(Router router) {
        router.get(ApiConfig.instance().getPath("/bank/list"))
                .handler(AuthRouter::authorizeSupport)
                .handler(BankRouter::getListBankByState);
    }
    public static void adminAuthApi(Router router) {

    }
    public static void superAdminAuthApi(Router router) {
        router.post(ApiConfig.instance().getPath("/bank/login"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSuperAdmin)
                .handler(BankRouter::login);
        router.post(ApiConfig.instance().getPath("/bank/commit"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSuperAdmin)
                .handler(BankRouter::commitOTP);
        router.post(ApiConfig.instance().getPath("/bank/disable"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSuperAdmin)
                .handler(BankRouter::disableBank);
        router.post(ApiConfig.instance().getPath("/bank/wait_to_work"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSuperAdmin)
                .handler(BankRouter::waitToWorkBank);
        router.post(ApiConfig.instance().getPath("/bank/work"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSuperAdmin)
                .handler(BankRouter::startWorkingBank);
    }
    public static void systemAdminAuthApi(Router router) {

    }
}
