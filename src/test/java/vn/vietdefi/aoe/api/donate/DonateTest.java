package vn.vietdefi.aoe.api.donate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.aoe.services.match.MatchConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class DonateTest {
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
            baseUrl = "http://192.168.1.14:8000/aoe";
            username = "0384556555";
            password = "12344321";
            star = 100000;
            amount = 1000;
        }
        @RepeatedTest(1)
        void repeatTest1(){

        }
        @Test
        public void test0(){
            Common.deleleUser(baseUrl, username, password);
            JsonObject response = Common.registerUserSuccess(baseUrl, username, password);
            JsonObject user = response.getAsJsonObject("data");
            Common.addStarToWallet(baseUrl, user.get("id").getAsLong(), star);
            testDonateGamer(user);
            testDonateCaster(user);
            long matchId = createMatch(user).getAsJsonObject("data").get("id").getAsLong();
            testDonateMatch(user, matchId);
//            testListDonateByTarget(user, matchId);
            testListTopDonateByTarget(user, matchId);
            testListTopAllDonate(user);
            deleteMatch(matchId);
            testDonateLeague(user);
            testFilterStatisticDonate();
        }

        public void testDonateGamer(JsonObject user) {
            /*Get gamer*/
            String url = new StringBuilder(baseUrl)
                    .append("/gamer/list?page=").append(1).toString();
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.get(url);
            DebugLogger.info("{}",response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject gamers = response.getAsJsonObject("data");
            Assertions.assertTrue(gamers.getAsJsonArray("gamer").size() > 0);
            /*Test donate gamer*/
            JsonObject payload = new JsonObject();
            payload.addProperty("targetId", gamers.getAsJsonArray("gamer").get(0).getAsJsonObject().get("user_id").getAsLong());
            payload.addProperty("star", amount);
            payload.addProperty("message", "Donate gamer");
            String donateGamerURL = new StringBuilder(baseUrl).append("/donate/gamer").toString();
            DebugLogger.info("{}", donateGamerURL);
            response = OkHttpUtil.postJson(donateGamerURL, payload.toString(), Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            int balance = Common.getStartWallet(baseUrl, user).getAsJsonObject("data").get("balance").getAsInt();
            DebugLogger.info("{}", balance);
            int balanceAfterDonate = star - response.getAsJsonObject("data")
                    .get("amount").getAsInt();
            Assertions.assertEquals(balance, balanceAfterDonate);
            star = balanceAfterDonate;
        }

        public void testDonateCaster(JsonObject user) {
            /*Get list caster*/
            String url = new StringBuilder(baseUrl)
                    .append("/caster/list?page=1").toString();
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.get(url);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonArray casters = response.getAsJsonObject("data").getAsJsonArray("caster");
            Assertions.assertTrue(casters.size() > 0);
            DebugLogger.info("{}", response);

            /*test donate caster*/
            JsonObject payload = new JsonObject();
            payload.addProperty("targetId", casters.get(0).getAsJsonObject().get("user_id").getAsLong());
            payload.addProperty("star", amount);
            payload.addProperty("message", "Donate caster");
            String donateGamerURL = new StringBuilder(baseUrl).append("/donate/caster").toString();
            DebugLogger.info("{}", donateGamerURL);
            response = OkHttpUtil.postJson(donateGamerURL, payload.toString(), Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            int balance = Common.getStartWallet(baseUrl, user).getAsJsonObject("data").get("balance").getAsInt();
            DebugLogger.info("{}", balance);
            int balanceAfterDonate = star - response.getAsJsonObject("data")
                    .get("amount").getAsInt();
            Assertions.assertEquals(balance, balanceAfterDonate);
            star = balanceAfterDonate;
        }

        public JsonObject createMatch(JsonObject user) {
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
             JsonObject response = OkHttpUtil.postJson(createMatchURL, payload.toString(), Common.createHeaderAdmin());
            long matchId = response.getAsJsonObject("data").get("id").getAsLong();
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            return response;
        }

        public void testDonateMatch(JsonObject user, long matchId) {
            JsonObject payload = new JsonObject();
            payload.addProperty("targetId", matchId);
            payload.addProperty("star", amount);
            String donateMatchURL = new StringBuilder(baseUrl).append("/donate/match").toString();
            DebugLogger.info("{}", donateMatchURL);
            JsonObject response = OkHttpUtil.postJson(donateMatchURL, payload.toString(), Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            int balance = Common.getStartWallet(baseUrl, user).getAsJsonObject("data").get("balance").getAsInt();
            DebugLogger.info("{}", balance);
            int balanceAfterDonate = star - response.getAsJsonObject("data")
                    .get("amount").getAsInt();
            Assertions.assertEquals(balance, balanceAfterDonate);
            star = balanceAfterDonate;
        }

        public void deleteMatch(long matchId) {
            JsonObject payload = new JsonObject();
            payload.addProperty("match_id", matchId);
            String deleteMatchURL = new StringBuilder(baseUrl).append("/match/delete").toString();
            DebugLogger.info("{}", deleteMatchURL);
            JsonObject response = OkHttpUtil.postJson(deleteMatchURL, payload.toString(), Common.createHeaderSystemAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }

        public void testListDonateByTarget(JsonObject user, long matchId) {
            int page = 1;
            String listDonateURL = new StringBuilder(baseUrl).append("/donate/list")
                    .append("?target_id=").append(matchId).append("&page=").append(page).toString();
            DebugLogger.info("{}", listDonateURL);
            JsonObject response = OkHttpUtil.get(listDonateURL, Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertFalse(response.getAsJsonArray("data").isEmpty());
        }

        public void testListTopDonateByTarget(JsonObject user, long matchId) {
            long from = 0L;
            long to = 1792678938104L;
            String listTopDonateURL = new StringBuilder(baseUrl).append("/donate/list-top")
                    .append("?target_id=").append(matchId).toString();
            DebugLogger.info("{}", listTopDonateURL);
            JsonObject response = OkHttpUtil.get(listTopDonateURL, Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertFalse(response.getAsJsonArray("data").isEmpty());
        }

        public void testListTopAllDonate(JsonObject user) {
            String listTopAllDonateURL = new StringBuilder(baseUrl).append("/donate/list-top-all").toString();
            DebugLogger.info("{}", listTopAllDonateURL);
            JsonObject response = OkHttpUtil.get(listTopAllDonateURL, Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertFalse(response.getAsJsonArray("data").isEmpty());
        }

        public void testDonateLeague(JsonObject user) {
            JsonObject payload = new JsonObject();
            payload.addProperty("name", "VN-CN League");
            payload.addProperty("banner", "https://");
            payload.addProperty("star_current", 1000);
            payload.addProperty("star_default_online", 3000);
            payload.addProperty("star_default_offline", 10000);
            payload.addProperty("time_expired", 1692453732898L);
            payload.add("detail", new JsonObject());
            payload.addProperty("donate_benefit", "text html");

            StringBuilder url = new StringBuilder(baseUrl).append("/league/create");
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.postJson(url.toString(), payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            assert response != null;
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            long leagueId = response.getAsJsonObject("data").get("id").getAsLong();

            payload = new JsonObject();
            payload.addProperty("targetId", leagueId);
            payload.addProperty("star", amount);
            String donateMatchURL = new StringBuilder(baseUrl).append("/donate/league").toString();
            DebugLogger.info("{}", donateMatchURL);
            response = OkHttpUtil.postJson(donateMatchURL, payload.toString(), Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            int balance = Common.getStartWallet(baseUrl, user).getAsJsonObject("data").get("balance").getAsInt();
            DebugLogger.info("{}", balance);
            int balanceAfterDonate = star - response.getAsJsonObject("data")
                    .get("amount").getAsInt();
            Assertions.assertEquals(balance, balanceAfterDonate);
            star = balanceAfterDonate;
        }

        /*filter donate public*/
        public void testFilterStatisticDonate() {
            String listTopAllDonateURL = new StringBuilder(baseUrl).append("/donate/statistic/filter")
                    .append("?service=").append(10).append("&target_id=").append(100)
                    .append("&page=").append(1).toString();
            DebugLogger.info("{}", listTopAllDonateURL);
            JsonObject response = OkHttpUtil.get(listTopAllDonateURL,Common.createHeaderSupport());
            DebugLogger.info("{}", response);
            Assertions.assertFalse(response.getAsJsonObject("data").getAsJsonArray("donate").isEmpty());
        }

        @Test
        public void test1(){
            testFilterStatisticDonate();
        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
