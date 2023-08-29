package vn.vietdefi.aoe.api.matchsuggest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.aoe.services.matchsuggest.MatchSuggestConstant;
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
            baseUrl = "http://127.0.0.1:8000/aoe";
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
            Common.addStarToWallet(baseUrl, user.get("id").getAsLong(), star);

            long matchId = testCreateSuggestMatch(user).getAsJsonObject("data").get("id").getAsLong();
            testUpdateSuggestMatch(user, matchId);
            testGetSuggestMatchInfo(user, matchId);
            testListSuggestMatch(user);
            testAdminListSuggestMatch();
            testConfirmMatch(user, matchId);
            //testCancelSuggestMatch(user, matchId);
            deleteSuggestMatch(matchId);

        }
        public JsonObject testCreateSuggestMatch(JsonObject user) {
            JsonObject payload = new JsonObject();
            payload.addProperty("type", 1);
            payload.addProperty("format", 1);
            payload.add("team_player", new JsonArray());
            payload.add("detail", new JsonObject());
            payload.addProperty("amount", amount);
            String createSuggestMatchURL = new StringBuilder(baseUrl).append("/match/user/suggest/create").toString();
            DebugLogger.info("{}", createSuggestMatchURL);
            JsonObject response = OkHttpUtil.postJson(createSuggestMatchURL, payload.toString(), Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject startWallet = Common.getStartWallet(baseUrl, user);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertEquals(startWallet.getAsJsonObject("data").get("balance").getAsInt(),
                    star - amount);
            return response;
        }
        public void testUpdateSuggestMatch(JsonObject user, long matchId) {
            JsonObject payload = new JsonObject();
            payload.addProperty("id", matchId);
            payload.addProperty("state", MatchSuggestConstant.MATCH_SUGGEST_PENDING);
            payload.addProperty("amount", amount);
            String updateSuggestMatchURL = new StringBuilder(baseUrl).append("/match/user/suggest/update").toString();
            DebugLogger.info("{}", updateSuggestMatchURL);
            JsonObject response = OkHttpUtil.postJson(updateSuggestMatchURL, payload.toString(), Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }
        public void testListSuggestMatch(JsonObject user) {
            String listSuggestMatchURL = new StringBuilder(baseUrl).append("/match/user/suggest/list?state=10").toString();
            DebugLogger.info("{}", listSuggestMatchURL);
            JsonObject response = OkHttpUtil.get(listSuggestMatchURL, Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonArray listSuggestMatch = response.getAsJsonObject("data").get("match").getAsJsonArray();
            Assertions.assertFalse(listSuggestMatch.isEmpty());
        }
        public void testAdminListSuggestMatch() {
            String listSuggestMatchURL = new StringBuilder(baseUrl).append("/match/suggest/list?state=").append(MatchSuggestConstant.MATCH_SUGGEST_PENDING).toString();
            DebugLogger.info("{}", listSuggestMatchURL);
            JsonObject response = OkHttpUtil.get(listSuggestMatchURL, Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonArray listSuggestMatch = response.getAsJsonObject("data").get("match").getAsJsonArray();
            Assertions.assertFalse(listSuggestMatch.isEmpty());
        }

        public JsonObject testGetSuggestMatchInfo(JsonObject user, long matchId) {
            String listSuggestMatchURL = new StringBuilder(baseUrl).append("/match/user/suggest/info")
                    .append("?match_id=").append(matchId).toString();
            DebugLogger.info("{}", listSuggestMatchURL);
            JsonObject response = OkHttpUtil.get(listSuggestMatchURL, Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertEquals(matchId, response.getAsJsonObject("data").get("id").getAsLong());
            return response;
        }

        public void testConfirmMatch(JsonObject user, long matchId) {
            JsonObject oldStarWallet = Common.getStartWallet(baseUrl, user);
            DebugLogger.info("{}", oldStarWallet);
            JsonObject payload = new JsonObject();
            payload.addProperty("match_suggest_id", matchId);
            payload.addProperty("format", 1);
            payload.addProperty("type", 1);
            payload.addProperty("star_default", 1000);
            payload.add("detail", new JsonObject());
            payload.addProperty("time_expired", System.currentTimeMillis() + 6220800000L);
            JsonObject player1 = new JsonObject();
            player1.addProperty("user_id", 4);
            player1.addProperty("team", 1);
            player1.addProperty("nick_name", "A");
            player1.addProperty("avatar", "A");
            JsonArray teamPlayer = new JsonArray();
            teamPlayer.add(player1);
            teamPlayer.add(player1);
            payload.add("team_player",teamPlayer);

            String confirmMatchURL = new StringBuilder(baseUrl).append("/match/suggest/confirm").toString();
            DebugLogger.info("{}", confirmMatchURL);
            JsonObject response = OkHttpUtil.postJson(confirmMatchURL, payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject match = testGetSuggestMatchInfo(user, matchId);
            Assertions.assertEquals(MatchSuggestConstant.MATCH_SUGGEST_CONFIRM, match.getAsJsonObject("data").get("state").getAsInt());
            JsonObject newStarWallet = Common.getStartWallet(baseUrl, user);
            DebugLogger.info("{}", newStarWallet);
            Assertions.assertEquals(oldStarWallet.getAsJsonObject("data").get("balance").getAsInt(),
                    newStarWallet.getAsJsonObject("data").get("balance").getAsInt());
        }

        public void testCancelSuggestMatch(JsonObject user, long matchId) {
            JsonObject oldStarWallet = Common.getStartWallet(baseUrl, user);
            JsonObject payload = new JsonObject();
            payload.addProperty("match_id", matchId);
            String cancelMatchURL = new StringBuilder(baseUrl).append("/match/suggest/cancel").toString();
            DebugLogger.info("{}", cancelMatchURL);
            JsonObject response = OkHttpUtil.postJson(cancelMatchURL, payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject newStarWallet = Common.getStartWallet(baseUrl, user);
            Assertions.assertEquals(oldStarWallet.getAsJsonObject("data").get("balance").getAsInt() + amount,
                    newStarWallet.getAsJsonObject("data").get("balance").getAsInt());
        }

        public void deleteSuggestMatch(long matchId) {
            JsonObject payload = new JsonObject();
            payload.addProperty("match_id", matchId);
            String deleteMatchURL = new StringBuilder(baseUrl).append("/match/suggest/delete").toString();
            DebugLogger.info("{}", deleteMatchURL);
            JsonObject response = OkHttpUtil.postJson(deleteMatchURL, payload.toString(), Common.createHeaderSystemAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
