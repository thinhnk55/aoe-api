package vn.vietdefi.aoe.launcher;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.apache.log4j.xml.DOMConfigurator;
import vn.vietdefi.aoe.services.statistic.logic.StatisticController;
import vn.vietdefi.aoe.vertx.AoeVerticle;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.bank.logic.BankController;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;

public class AoeLauncher {
    public static Vertx vertx;
    public static void main(String[] args) {
        run();
    }
    public static void run(){
        try {
            initConfig();
            //BankController.instance().startLoop();
            StatisticController.instance();
            startHttpServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void initConfig() throws Exception {
        DOMConfigurator.configure("config/aoe/log/log4j.xml");
        HikariClients.instance().init("config/aoe/sql/databases.json"
                ,"config/aoe/sql/hikari.properties");
        ApiConfig.instance().init("config/aoe/http/http.json");
    }

    public static void startHttpServer() {
        int procs = Runtime.getRuntime().availableProcessors();
        VertxOptions vxOptions = new VertxOptions()
                .setBlockedThreadCheckInterval(30000);
        vertx = Vertx.vertx(vxOptions);
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setWorker(true).setWorkerPoolSize(procs * 2);
        vertx.deployVerticle(AoeVerticle.class.getName(),
                deploymentOptions.setInstances(procs * 2), event -> {
                    if (event.succeeded()) {
                        DebugLogger.info("Your Vert.x application is started!");
                    } else {
                        DebugLogger.error("Unable to start your application", event.cause());
                    }
                });
    }
}
