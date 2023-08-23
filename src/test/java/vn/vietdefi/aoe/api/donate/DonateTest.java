package vn.vietdefi.aoe.api.donate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.vertx.core.json.Json;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.aoe.services.match.MatchConstants;
import vn.vietdefi.aoe.services.star.StarConstant;
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
            baseUrl = "http://192.168.250.1:8000/aoe";
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
            testListDonateByTarget(user, matchId);
            testListTopDonateByTarget(user, matchId);
            testListTopAllDonate(user);
            deleteMatch(matchId);
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
            JsonArray casters = response.getAsJsonObject("data").getAsJsonArray("casters");
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
            payload.addProperty("suggester_id", user.get("id").getAsLong());
            payload.addProperty("state", MatchConstants.STATE_VOTING);
            payload.addProperty("create_time", System.currentTimeMillis());
            payload.add("team_player", new JsonArray());

            String startMatchURL = new StringBuilder(baseUrl).append("/admin/match/create").toString();
            DebugLogger.info("{}", startMatchURL);
            JsonObject response = OkHttpUtil.postJson(startMatchURL, payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            return response;
        }

        public void testDonateMatch(JsonObject user, long matchId) {
            JsonObject payload = new JsonObject();
            payload.addProperty("targetId", matchId);
            payload.addProperty("star", amount);
            payload.addProperty("message", "Donate match");
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
            String deleteMatchURL = new StringBuilder(baseUrl).append("/admin/match/delete").toString();
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

        @Test
        public void test1(){

        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
