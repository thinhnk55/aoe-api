package vn.vietdefi.aoe.api.match;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.aoe.services.match.MatchConstant;
import vn.vietdefi.common.BaseResponse;
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
            baseUrl = "http://192.168.1.14:8000/aoe";
            username = "0352555556";
            password = "12344321";
        }
        @RepeatedTest(1)
        void repeatTest1(){

        }
        @Test
        public void test0(){
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
            JsonObject player1 = new JsonObject();
            player1.addProperty("user_id", 4);
            player1.addProperty("team", 1);
            player1.addProperty("nick_name", "A");
            player1.addProperty("avatar", "A");
            JsonArray teamPlayer = new JsonArray();
            teamPlayer.add(player1);
            teamPlayer.add(player1);
            payload.add("team_player",teamPlayer);

            String createMatchURL = new StringBuilder(baseUrl).append("/match/create").toString();
            response = OkHttpUtil.postJson(createMatchURL, payload.toString(), Common.createHeaderAdmin());
            long matchId = response.getAsJsonObject("data").get("id").getAsLong();
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));

            /*test update match*/
            payload = new JsonObject();
            payload.addProperty("id", matchId);
            payload.add("detail", new JsonObject());
            payload.addProperty("type", 1);
            payload.addProperty("format", 2);
            payload.addProperty("time_expired", System.currentTimeMillis() + 6220800000L);
            payload.addProperty("star_default", 1000);
            payload.add("team_player", new JsonArray());

            String updateMatchURL = new StringBuilder(baseUrl).append("/match/update").toString();
            response = OkHttpUtil.postJson(updateMatchURL, payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
//            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));

            /*Test stop voting for match*/
            payload = new JsonObject();
            payload.addProperty("match_id", matchId);
            payload.addProperty("match_date", System.currentTimeMillis());
            String stopVotingMatchURL = new StringBuilder(baseUrl).append("/match/stop/vote").toString();
            response = OkHttpUtil.postJson(stopVotingMatchURL, payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject matchInfo = Common.getMatchById(baseUrl, user, matchId);
            Assertions.assertEquals(matchInfo.getAsJsonObject("data").get("state").getAsInt(),
                    MatchConstant.STATE_STOP_VOTING);

            /*Test start match*/
            payload = new JsonObject();
            payload.addProperty("match_id", matchId);
            String linkLivestream = "https://aoe.com";
            payload.addProperty("link_livestream", linkLivestream);
            String startMatchURL = new StringBuilder(baseUrl).append("/match/start").toString();
            response = OkHttpUtil.postJson(startMatchURL, payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            matchInfo = Common.getMatchById(baseUrl, user, matchId);
            Assertions.assertEquals(matchInfo.getAsJsonObject("data").get("state").getAsInt(),
                    MatchConstant.STATE_PLAYING);

            /*Test end match*/
            payload = new JsonObject();
            payload.addProperty("match_id", matchId);
            payload.add("result", new JsonArray());
            String endMatchURL = new StringBuilder(baseUrl).append("/match/end").toString();
            response = OkHttpUtil.postJson(endMatchURL, payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            matchInfo = Common.getMatchById(baseUrl, user, matchId);
            Assertions.assertEquals(matchInfo.getAsJsonObject("data").get("state").getAsInt(),
                    MatchConstant.STATE_FINISHED);

            /*Test update result for match*/
            payload = new JsonObject();
            payload.addProperty("match_id", matchId);
            JsonArray result = new JsonArray();
            payload.add("result", result);
            String updateResultMatchURL = new StringBuilder(baseUrl).append("/match/update/result").toString();
            response = OkHttpUtil.postJson(updateResultMatchURL, payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            matchInfo = Common.getMatchById(baseUrl, user, matchId);
            Assertions.assertEquals(matchInfo.getAsJsonObject("data").get("detail").getAsJsonObject()
                    .get("result"), result);

            /*Test cancel match*/
            payload = new JsonObject();
            payload.addProperty("match_id", matchId);
            String cancelMatchURL = new StringBuilder(baseUrl).append("/match/cancel").toString();
            response = OkHttpUtil.postJson(cancelMatchURL, payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("Cancel match{}", response);
            Assertions.assertEquals(11, response.get("error").getAsInt());
            matchInfo = Common.getMatchById(baseUrl, user, matchId);
            Assertions.assertEquals(matchInfo.getAsJsonObject("data").get("state").getAsInt(),
                    MatchConstant.STATE_FINISHED);

            /*Test get list match*/
            int state = MatchConstant.STATE_FINISHED;
            int page = 1;
            String getListMatchURL = new StringBuilder(baseUrl).append("/match/list/state")
                    .append("?state=").append(state).append("&page=").append(page).toString();
            response = OkHttpUtil.get(getListMatchURL);
            DebugLogger.info("List by state {}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertFalse(response.getAsJsonObject("data").getAsJsonArray("match").isEmpty());

            /*Delete match after test*/
            payload = new JsonObject();
            payload.addProperty("match_id", matchId);
            String deleteMatchURL = new StringBuilder(baseUrl).append("/match/delete").toString();
            response = OkHttpUtil.postJson(deleteMatchURL, payload.toString(), Common.createHeaderSystemAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));

        }
        public void getOutStandingMatch() {
            /*Test get outstanding match*/
            String getOutstandingMatchURL = new StringBuilder(baseUrl).append("/match/outstanding").toString();
            JsonObject response = OkHttpUtil.get(getOutstandingMatchURL);
            DebugLogger.info("Outstanding match {}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }

        @Test
        public void test1(){
            getOutStandingMatch();
        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }

}
