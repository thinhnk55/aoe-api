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
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));

            /*Test stop voting for match*/
            payload = new JsonObject();
            payload.addProperty("match_id", matchId);
            payload.addProperty("match_date", System.currentTimeMillis());
            String stopVotingMatchURL = new StringBuilder(baseUrl).append("/match/voting/stop").toString();
            response = OkHttpUtil.postJson(stopVotingMatchURL, payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject matchInfo = Common.getMatchById(baseUrl, user, matchId);
            Assertions.assertEquals(matchInfo.getAsJsonObject("data").get("state").getAsInt(),
                    MatchConstants.STATE_STOP_VOTING);

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
                    MatchConstants.STATE_PLAYING);

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
                    MatchConstants.STATE_FINISHED);

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
                    MatchConstants.STATE_FINISHED);

            /*Test get list match*/
            int state = MatchConstants.STATE_FINISHED;
            int page = 1;
            String getListMatchURL = new StringBuilder(baseUrl).append("/match/getlist/state")
                    .append("?state=").append(state).append("&page=").append(page).toString();
            response = OkHttpUtil.get(getListMatchURL, Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertFalse(response.getAsJsonObject("data").getAsJsonArray("match").isEmpty());

            /*Test get outstanding match*/
            String getOutstandingMatchURL = new StringBuilder(baseUrl).append("/match/outstanding")
                    .append("?state=").append(state).append("&page=").append(page).toString();
            response = OkHttpUtil.get(getOutstandingMatchURL, Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));

            /*Delete match after test*/
            payload = new JsonObject();
            payload.addProperty("match_id", matchId);
            String deleteMatchURL = new StringBuilder(baseUrl).append("/admin/match/delete").toString();
            response = OkHttpUtil.postJson(deleteMatchURL, payload.toString(), Common.createHeaderSystemAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));

        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }

}
