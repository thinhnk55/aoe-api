package vn.vietdefi.aoe.api.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.aoe.services.league.LeagueConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class DataTest {
    @BeforeAll
    static void init(){
        try {
            DOMConfigurator.configure("config/aoe/log/log4j.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nested
    class AoeAuthTest1 {
        String baseUrl;
        String username;
        String password;
        int star;
        int amount;

        @BeforeEach
        void init() {
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://192.168.1.19:8000/aoe";
            username = "0352555556";
            password = "12344321";
            star = 100000;
            amount = 1000;
        }

        @RepeatedTest(1)
        void repeatTest1() {

        }

        @Test
        public void test0() {
            getContact();
//            updateContact();
//            getContact();
        }

        public void createContact() {
            JsonObject payload = new JsonObject();
            payload.addProperty("phone", "0326894941");
            payload.addProperty("facebook", "https://");

            StringBuilder url = new StringBuilder(baseUrl).append("/data/contact/create");
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.postJson(url.toString(), payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            assert response != null;
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }

        public void updateContact() {
            JsonObject payload = new JsonObject();
            payload.addProperty("phone", "0326894941");
            payload.addProperty("facebook", "https://");

            StringBuilder url = new StringBuilder(baseUrl).append("/data/contact/update");
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.postJson(url.toString(), payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            assert response != null;
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }

        public void getContact() {
            StringBuilder url = new StringBuilder(baseUrl).append("/data/contact/get");
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.get(url.toString());
            DebugLogger.info("{}", response);
            assert response != null;
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }


        @Test
        public void test1() {

        }
    }
    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
