package vn.vietdefi.aoe.services;

import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.bank.BankServices;
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
//            long now = System.currentTimeMillis();
//            int service = 11;
//            long from = 1692373678535L;
//            String phone = "";
//            JsonObject response = AoeServices.donateService.filterListDonate(phone,from,now,service,1,15);
//            long gap = System.currentTimeMillis()-now;
//            DebugLogger.info("{} {}", gap, response);
            JsonObject response = BankServices.bankService.getOneWorkingBankAccount();
            DebugLogger.info("{} ", response);
        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
