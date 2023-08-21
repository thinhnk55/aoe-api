package vn.vietdefi.aoe.api;

import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.api.services.auth.AuthService;
import vn.vietdefi.api.services.auth.IAuthService;
import vn.vietdefi.api.services.auth.UserConstant;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthApiTest {
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
    class AoeAuthTest1{
        String baseUrl;
        @BeforeEach
        void init(){
            baseUrl = "https://api.godoo.asia/aoe";
        }
        @RepeatedTest(1)
        void repeatTest1(){
        }
        @Test
        public void test0(){
            //test register
        }
        @Test
        public void test1(){
            //test login
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
