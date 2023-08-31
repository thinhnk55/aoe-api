package vn.vietdefi.aoe.api.league;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.aoe.services.league.LeagueConstant;
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
            baseUrl = "http://192.168.1.14:8000/aoe";
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

            JsonObject league = createLeague();
            long leagueId = league.getAsJsonObject("data").get("id").getAsLong();
            updateLeague(leagueId);
            getLeagueInfo(leagueId);
            getListLeague();
            getListAllLeague();
            stopVoteLeague(leagueId);
            startLeague(leagueId);
            endLeague(leagueId);
            cancelLeague(leagueId);
            deleteLeague(leagueId);

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
            payload.add("donate_benefit", new JsonArray());

            StringBuilder url = new StringBuilder(baseUrl).append("/league/create");
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.postJson(url.toString(), payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            assert response != null;
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            return response;
        }

        public void updateLeague(long leagueId) {
            JsonObject payload = new JsonObject();
            payload.addProperty("id", leagueId);
            payload.addProperty("name", "VN-CN League");
            payload.addProperty("banner", "https://");
            payload.addProperty("star_current", 1000);
            payload.addProperty("star_default_online", 3000);
            payload.addProperty("star_default_offline", 10000);
            payload.addProperty("time_expired", 1692453732898L);
            payload.add("detail", new JsonObject());
            payload.addProperty("donate_benefit", "text html");

            StringBuilder url = new StringBuilder(baseUrl).append("/league/update");
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.postJson(url.toString(), payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            assert response != null;
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }

        public JsonObject getLeagueInfo(long leagueId) {
            StringBuilder url = new StringBuilder(baseUrl).append("/league/get").append("?id=").append(leagueId);
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.get(url.toString());
            DebugLogger.info("{}", response);
            assert response != null;
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertEquals(leagueId, response.getAsJsonObject("data").get("id").getAsLong());
            return response;
        }

        public void getListAllLeague() {
            StringBuilder url = new StringBuilder(baseUrl).append("/league/list-all").append("?page=").append(1);
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.get(url.toString());
            DebugLogger.info("{}", response);
            assert response != null;
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertFalse(response.getAsJsonObject("data").getAsJsonArray("league").isEmpty());
        }

        public void getListLeague() {
            StringBuilder url = new StringBuilder(baseUrl).append("/league/list").append("?state=")
                    .append(LeagueConstant.STATE_VOTING).append("&page=").append(1);
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.get(url.toString());
            DebugLogger.info("{}", response);
            assert response != null;
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertFalse(response.getAsJsonObject("data").getAsJsonArray("league").isEmpty());
        }

        public void stopVoteLeague(long leagueId) {
            JsonObject payload = new JsonObject();
            payload.addProperty("id", leagueId);
            payload.addProperty("start_date", 1692453732898L);

            StringBuilder url = new StringBuilder(baseUrl).append("/league/stop-vote");
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.postJson(url.toString(), payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            assert response != null;
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject league = getLeagueInfo(leagueId);
            Assertions.assertEquals(league.getAsJsonObject("data").get("state").getAsInt(),
                    LeagueConstant.STATE_STOP_VOTING);
        }

        public void startLeague(long leagueId) {
            JsonObject payload = new JsonObject();
            payload.addProperty("id", leagueId);
            payload.addProperty("link_livestream", "https://");

            StringBuilder url = new StringBuilder(baseUrl).append("/league/start");
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.postJson(url.toString(), payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            assert response != null;
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject league = getLeagueInfo(leagueId);
            Assertions.assertEquals(league.getAsJsonObject("data").get("state").getAsInt(),
                    LeagueConstant.STATE_PLAYING);
        }

        public void endLeague(long leagueId) {
            JsonObject payload = new JsonObject();
            payload.addProperty("id", leagueId);

            StringBuilder url = new StringBuilder(baseUrl).append("/league/end");
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.postJson(url.toString(), payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            assert response != null;
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject league = getLeagueInfo(leagueId);
            Assertions.assertEquals(league.getAsJsonObject("data").get("state").getAsInt(),
                    LeagueConstant.STATE_FINISHED);
        }

        public void cancelLeague(long leagueId) {
            JsonObject payload = new JsonObject();
            payload.addProperty("id", leagueId);

            StringBuilder url = new StringBuilder(baseUrl).append("/league/cancel");
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.postJson(url.toString(), payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            assert response != null;
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject league = getLeagueInfo(leagueId);
            Assertions.assertEquals(league.getAsJsonObject("data").get("state").getAsInt(),
                    LeagueConstant.STATE_CANCELLED);
        }

        public void deleteLeague(long leagueId) {
            JsonObject payload = new JsonObject();
            payload.addProperty("id", leagueId);

            StringBuilder url = new StringBuilder(baseUrl).append("/league/delete");
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.postJson(url.toString(), payload.toString(), Common.createHeaderSystemAdmin());
            DebugLogger.info("{}", response);
            assert response != null;
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }

        public void getOutStandingLeague() {
            StringBuilder url = new StringBuilder(baseUrl).append("/league/outstanding");
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.get(url.toString());
            DebugLogger.info("{}", response);
            assert response != null;
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }

        @Test
        public void test1() {
            getOutStandingLeague();
        }
    }
    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
