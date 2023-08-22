package vn.vietdefi.aoe.api.match;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.aoe.services.match.MatchConstants;
import vn.vietdefi.aoe.services.profile.ProfileConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class MatchTest {
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
            baseUrl = "http://192.168.1.23:8000/aoe";
            username = "086888444";
            password = "12344321";
        }
        @RepeatedTest(1)
        void repeatTest1(){

        }
        @Test
        public void test0(){

        }
        @Test
        public void test1(){
            JsonObject response = Common.deleleUser(baseUrl, username, password);
            DebugLogger.info("{}", response);
            response = Common.registerUserSuccess(baseUrl, username, password);
            DebugLogger.info("{}", response);
            JsonObject user = response.getAsJsonObject("data");

            /*test create match*/
            JsonObject payload = new JsonObject();
            payload.addProperty("format", 1);
            payload.addProperty("type", 1);
            payload.addProperty("star_default", 1000);
            payload.add("detail", new JsonObject());
            payload.addProperty("time_expired", System.currentTimeMillis() + 6220800000L);
            payload.addProperty("suggester_id", 45);
            payload.addProperty("state", MatchConstants.STATE_VOTING);
            payload.addProperty("create_time", System.currentTimeMillis());
            payload.add("team_player", new JsonArray());

            String createMatchURL = new StringBuilder(baseUrl).append("/admin/match/create").toString();
            response = OkHttpUtil.postJson(createMatchURL, payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));

            /*test update match*/
            payload = new JsonObject();
            payload.addProperty("format", 2);
            payload.addProperty("type", 1);
            payload.addProperty("star_default", 1000);
            payload.add("detail", new JsonObject());
            payload.addProperty("time_expired", System.currentTimeMillis() + 6220800000L);
            payload.addProperty("suggester_id", 45);
            payload.addProperty("state", MatchConstants.STATE_VOTING);
            payload.addProperty("create_time", System.currentTimeMillis());
            payload.add("team_player", new JsonArray());

            String updateMatchURL = new StringBuilder(baseUrl).append("/admin/match/update").toString();
            response = OkHttpUtil.postJson(updateMatchURL, payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertEquals(2, response.getAsJsonObject("data").get("format").getAsInt());

            /**/

        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
