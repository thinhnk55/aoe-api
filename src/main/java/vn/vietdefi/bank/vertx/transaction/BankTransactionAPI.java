package vn.vietdefi.bank.vertx.transaction;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.api.vertx.router.AuthRouter;

public class BankTransactionAPI {
    public static void configAPI(Router router) {
        publicApi(router);
        userAuthApi(router);
        supportAuthApi(router);
        adminAuthApi(router);
        superAdminAuthApi(router);
        systemAdminAuthApi(router);
    }
    private static void publicApi(Router router){

    }
    public static void userAuthApi(Router router) {

    }
    public static void supportAuthApi(Router router) {
        router.get(ApiConfig.instance().getPath("/bank/transaction/error"))
                .handler(AuthRouter::authorizeSupport)
                .handler(BankTransactionRouter::listBankTransactionError);
    }
    public static void adminAuthApi(Router router) {

    }
    public static void superAdminAuthApi(Router router) {
        router.post(ApiConfig.instance().getPath("/bank/transaction/fix"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSuperAdmin)
                .handler(BankTransactionRouter::fixTransaction);
    }
    public static void systemAdminAuthApi(Router router) {
        router.post(ApiConfig.instance().getPath("/bank/transaction/create"))
                .handler(BodyHandler.create(false))
                .handler(AuthRouter::authorizeSystemAdmin)
                .handler(BankTransactionRouter::createBankTransaction);
    }
}
