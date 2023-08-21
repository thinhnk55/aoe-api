package vn.vietdefi.aoe.api;

import com.google.gson.JsonObject;
import io.vertx.core.json.Json;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.common.BaseResponse;
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
        long system_admin_id = 2;
        String system_admin_token = "0os6nq11fovc3cgyoab6x2fbk3zpl6tn";
        @BeforeEach
        void init(){
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://192.168.1.99:8000/aoe";
            username = "0836993400";
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
            //test case
            //prepare environment
            String loginUrl = new StringBuilder(baseUrl)
                    .append("/auth/login").toString();
            String deleteUrl = new StringBuilder(baseUrl)
                    .append("/auth/delete_user").toString();
            String registerUrl = new StringBuilder(baseUrl)
                    .append("/auth/register").toString();
            JsonObject payload = new JsonObject();
            payload.addProperty("username", username);
            payload.addProperty("password", password);
            JsonObject response = OkHttpUtil.postJson(loginUrl, payload.toString());
            if(BaseResponse.isSuccessFullMessage(response)){
                long id = response.getAsJsonObject("data").get("id").getAsLong();
                payload = new JsonObject();
                payload.addProperty("user_id", id);
                response = OkHttpUtil.postJson(deleteUrl, payload.toString(), AuthTestUtil.createHeader(system_admin_id, system_admin_token));
                DebugLogger.info("{}", response);
            }
            //test
            payload = new JsonObject();
            payload.addProperty("username", username);
            payload.addProperty("password", password);
            response = OkHttpUtil.postJson(registerUrl, payload.toString());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            String token = response.getAsJsonObject("data").get("token").getAsString();
            Assertions.assertNotNull(token);
            long id = response.getAsJsonObject("data").get("id").getAsLong();
            Assertions.assertNotNull(id);
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
