package vn.vietdefi.aoe.api.matchsuggest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.aoe.services.match.MatchConstants;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class MatchSuggestTest {
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
        int star;
        int amount;

        @BeforeEach
        void init(){
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://192.168.250.1:8000/aoe";
            username = "0352555556";
            password = "12344321";
            star = 100000;
            amount = 1000;
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

            int star = 100000;
            response = Common.addStarToWallet(baseUrl, user.get("id").getAsLong(), star);
            DebugLogger.info("Add star to wallet: {}", response);


        }
        public JsonObject testCreateSuggestMatch(JsonObject user) {
            JsonObject payload = new JsonObject();
            int amount = 1000;
            payload.addProperty("amount", amount);
            String createSuggestMatchURL = new StringBuilder(baseUrl).append("/match/user/suggest").toString();
            DebugLogger.info("{}", createSuggestMatchURL);
            JsonObject response = OkHttpUtil.postJson(createSuggestMatchURL, payload.toString(), Common.createHeader(user));
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            response = Common.getStartWallet(baseUrl, user);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            return response;
        }
        public void testUpdateSuggestMatch(JsonObject user) {
            JsonObject payload = new JsonObject();
            int amount = 1000;
            payload.addProperty("amount", amount);
            String updateSuggestMatchURL = new StringBuilder(baseUrl).append("/match/user/suggest/update").toString();
            DebugLogger.info("{}", updateSuggestMatchURL);
            JsonObject response = OkHttpUtil.postJson(updateSuggestMatchURL, payload.toString(), Common.createHeader(user));
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }
        public void testListSuggestMatch(JsonObject user) {
            String listSuggestMatchURL = new StringBuilder(baseUrl).append("/match/user/suggest/list").toString();
            DebugLogger.info("{}", listSuggestMatchURL);
            JsonObject response = OkHttpUtil.get(listSuggestMatchURL, Common.createHeader(user));
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonArray listSuggestMatch = response.getAsJsonObject("data").get("match").getAsJsonArray();
            Assertions.assertFalse(listSuggestMatch.isEmpty());
        }
        public void testGetSuggestMatchInfo(JsonObject user, long matchId) {
            String listSuggestMatchURL = new StringBuilder(baseUrl).append("/match/user/suggest/info")
                    .append("?match_id=").append(matchId).toString();
            DebugLogger.info("{}", listSuggestMatchURL);
            JsonObject response = OkHttpUtil.get(listSuggestMatchURL, Common.createHeader(user));
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertEquals(matchId, response.getAsJsonObject("data").get("id").getAsLong());
        }

        public void testConfirmMatch(long matchId) {

            String confirmMatchURL = new StringBuilder(baseUrl).append("/match/confirm").toString();

        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
