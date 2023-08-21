package vn.vietdefi.aoe.services;

import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.services.profile.ProfileConstant;
import vn.vietdefi.api.services.ApiServices;
import vn.vietdefi.api.services.auth.UserConstant;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;

public class AoeAuthTest {
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
            ApiServices.authService.changeStatus(6, UserConstant.STATUS_ACCOUNT_GENERATE);
            JsonObject data = new JsonObject();
            data.addProperty("username", "0836993400");
            data.addProperty("password", "12345678");
            JsonObject response = AoeServices.aoeAuthService
                    .register(data);
            DebugLogger.info("{}", response);
            ApiServices.authService.changeRole(6, UserConstant.ROLE_SUPER_ADMIN);
            response = AoeServices.aoeAuthService
                    .login(data);
            DebugLogger.info("{}", response);
            ApiServices.authService.changeStatus(6, UserConstant.STATUS_ACCOUNT_GENERATE);
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
