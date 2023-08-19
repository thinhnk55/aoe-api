package vn.vietdefi.aoe.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.bank.vertx.BankHttpAPI;
import vn.vietdefi.websocket.WebsocketServer;
import java.util.HashSet;
import java.util.Set;

public class AoeVerticle extends AbstractVerticle {
    private HttpServer httpServer;
    private HttpServer websocket;


    @Override
    public void start() {
        Router router = Router.router(vertx);
        crossAccessControl(router);
        BankHttpAPI.configAPI(router);
        AoeAPI.configAPI(router);
        router.get(ApiConfig.instance().getPath("/test")).handler(this::test);
        httpServer = vertx.createHttpServer()
                .requestHandler(router)
                .listen(ApiConfig.instance().http_port).result();
        websocket = vertx.createHttpServer()
                .webSocketHandler(WebsocketServer::handle)
                .listen(ApiConfig.instance().websocket_port).result();
    }

    @Override
    public void stop() {
        if (websocket != null) websocket.close();
        if(httpServer != null) httpServer.close();
    }
    public void crossAccessControl(Router router) {
        Set<String> allowedHeaders = new HashSet<>();
        allowedHeaders.add("*");
        Set<HttpMethod> allowedMethods = new HashSet<>();
        allowedMethods.add(HttpMethod.GET);
        allowedMethods.add(HttpMethod.POST);
        allowedMethods.add(HttpMethod.OPTIONS);
        router.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowedHeaders)
                .allowedMethods(allowedMethods)
                .allowCredentials(true));
    }
    private void test(RoutingContext rc) {
        rc.response().end("OK");
    }
}