package vn.vietdefi.aoe.api.auth;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class AuthApiTest {
    @BeforeAll
    static void init(){
        try {
            DOMConfigurator.configure("config/aoe/log/log4j.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nested
    class AoeAuthTest1{
        String baseUrl;
        String username;
        String password;
        @BeforeEach
        void init(){
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://192.168.1.99:8000/aoe";
            username = "086888444";
            password = "12344321";
        }
        @RepeatedTest(1)
        void repeatTest1(){

        }
        @Test
        public void test0(){
            String url = new StringBuilder(baseUrl)
                    .append("/test").toString();
            String response = OkHttpUtil.getStringResponse(url);
            DebugLogger.info(response);
            Assertions.assertEquals(response, "OK");
        }
        @Test
        public void test1(){
            Common.deleleUser(baseUrl, username, password);
            Common.registerUserSuccess(baseUrl, username, password);
            Common.loginUserSuccess(baseUrl, username, password);
            Common.loginUserFailed(baseUrl, username, password + "123");
            Common.deleleUser(baseUrl, username, password);
        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
