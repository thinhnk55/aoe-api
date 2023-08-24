package vn.vietdefi.aoe.api.league;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.aoe.services.matchsuggest.MatchSuggestConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class LeagueTest {
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
            JsonObject response = Common.deleleUser(baseUrl, username, password);
            DebugLogger.info("{}", response);
            response = Common.registerUserSuccess(baseUrl, username, password);
            DebugLogger.info("{}", response);
            JsonObject user = response.getAsJsonObject("data");
            Common.addStarToWallet(baseUrl, user.get("id").getAsLong(), star);
        }

        public JsonObject createLeague() {
            JsonObject payload = new JsonObject();
            payload.addProperty("name", "VN-CN League");
            payload.addProperty("banner", "https://");
            payload.addProperty("star_current", 1000);
            payload.addProperty("star_default_online", 3000);
            payload.addProperty("star_default_offline", 10000);
            payload.addProperty("time_expired", 1692453732898L);
            payload.add("detail", new JsonObject());
            return  null;

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
