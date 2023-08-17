package vn.vietdefi.bank.vertx;

import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import vn.vietdefi.api.vertx.ApiConfig;

public class BankHttpAPI {
    public static void configAPI(Router router) {
        bankApi(router);
    }
    private static void bankApi(Router router){
        router.post(ApiConfig.instance().getPath("/bank/timo/login"))
                .handler(BodyHandler.create(false))
                .handler(BankRouter::timoLogin);
        router.post(ApiConfig.instance().getPath("/bank/timo/commit"))
                .handler(BodyHandler.create(false))
                .handler(BankRouter::timoCommit);
        router.get(ApiConfig.instance().getPath("/bank/list"))
                .handler(BankRouter::getListBank);
        router.post(ApiConfig.instance().getPath("/bank/add"))
                .handler(BodyHandler.create(false))
                .handler(BankRouter::addBank);
        router.post(ApiConfig.instance().getPath("/bank/update"))
                .handler(BodyHandler.create(false))
                .handler(BankRouter::updateBank);
        router.post(ApiConfig.instance().getPath("/bank/select"))
                .handler(BodyHandler.create(false))
                .handler(BankRouter::selectBank);
    }
}
