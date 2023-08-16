package vn.vietdefi.api.services;

import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;
import vn.vietdefi.util.string.StringUtil;

public class AuthTest {
    @BeforeAll
    static void init(){
        try {
            DOMConfigurator.configure("config/aoe/log/log4j.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nested
    class AuthTest1{
        String baseUrl;
        @BeforeEach
        void init(){
            baseUrl = "http://192.168.1.99:8000/aoe";
        }
        @RepeatedTest(1)
        void repeatTest1(){
        }
        @Test
        public void test0(){
            String registerUrl = baseUrl + "/register";
            JsonObject json = new JsonObject();
            String username = StringUtil.randomString(6);
            String password = StringUtil.randomString(6);
            json.addProperty("username",username);
            json.addProperty("password",password);
            JsonObject response = OkHttpUtil.postJson(registerUrl, json.toString(), null);
            DebugLogger.info("registerUrl {} {}", json.toString(), response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            String loginUrl = baseUrl + "/login";
            response = OkHttpUtil.postJson(loginUrl, json.toString(), null);
            DebugLogger.info("loginUrl {} {}", json.toString(), response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }
        @Test
        public void test1(){

        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
