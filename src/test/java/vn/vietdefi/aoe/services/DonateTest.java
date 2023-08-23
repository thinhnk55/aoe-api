package vn.vietdefi.aoe.services;

import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
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
            JsonObject response = AoeServices.gamerService.gamerUpdateStatistic(1000);
            DebugLogger.info("{}", response);
        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
