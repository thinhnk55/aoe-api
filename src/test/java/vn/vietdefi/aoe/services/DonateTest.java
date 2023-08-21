package vn.vietdefi.aoe.services;

import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.services.donate.DonateService;
import vn.vietdefi.aoe.services.donate.IDonateService;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;

public class DonateTest {
    @BeforeAll
    static void init(){
        try {
            DOMConfigurator.configure("config/aoe/log/log4j.xml");
            HikariClients.instance().init("config/aoe/sql/databases.json"
                    ,"config/aoe/sql/hikari.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nested
    class DonateTest1{
        @BeforeEach
        void init(){

        }
        @RepeatedTest(1)
        void repeatTest1(){
        }
        @Test
        public void test0(){
            JsonObject response = AoeServices.starService.exchangeStar(1, StarConstant.SERVICE_STAR_RECHARGE, 100000,
                    0);
            DebugLogger.info("{}", response);
            IDonateService donateService = new DonateService();
            response = AoeServices.starService.getStarWalletByUserId(5);
            DebugLogger.info("{}", response);
            response = donateService.donate(1,100, StarConstant.SERVICE_DONATE_CASTER,
                    5, "");
            DebugLogger.info("{}", response);
        }
        @Test
        public void test1(){

        }
        @Test
        public void test2(){

        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
