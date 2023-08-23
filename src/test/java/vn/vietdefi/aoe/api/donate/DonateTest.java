package vn.vietdefi.aoe.api.donate;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.aoe.services.AoeServices;
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

        @BeforeEach
        void init(){
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://127.0.0.1:8000/aoe";
            username = "0384556555";
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
            Assertions.assertNotNull(user.get("star").getAsJsonObject());

            /*Add star to star wallet*/
            JsonObject payload = new JsonObject();
            payload.addProperty("user_id", user.get("id").getAsLong());
            int service = StarConstant.SERVICE_STAR_RECHARGE;
            int star = 100000;
            payload.addProperty("service", service);
            payload.addProperty("amount", star);
            payload.addProperty("referId", 0);
            String adminExchangeStar = new StringBuilder(baseUrl).append("/star/admin/exchange").toString();
            response = OkHttpUtil.postJson(adminExchangeStar, payload.toString(), Common.createHeaderSystemAdmin());
            JsonObject transaction = response.getAsJsonObject("data");
            DebugLogger.info("{}", transaction);

            /*test donate gamer*/
            payload = new JsonObject();
            payload.addProperty("targetId", 135);
            int amount = 1000;
            payload.addProperty("star", amount);
            payload.addProperty("message", "Donate gamer");
                String donateGamerURL = new StringBuilder(baseUrl).append("/donate/gamer").toString();
            response = OkHttpUtil.postJson(donateGamerURL, payload.toString(), Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            int balance = Common.getStartWallet(baseUrl, user).getAsJsonObject("data").get("balance").getAsInt();
            DebugLogger.info("{}", balance);
            int balanceAfterDonate = star - response.getAsJsonObject("data")
                    .get("amount").getAsInt();
            Assertions.assertEquals(balance, balanceAfterDonate);

            /*test donate caster*/
            payload = new JsonObject();
            payload.addProperty("targetId", 154);
            payload.addProperty("star", amount);
            payload.addProperty("message", "Donate caster");
            String donateCasterURL = new StringBuilder(baseUrl).append("/donate/caster").toString();
            response = OkHttpUtil.postJson(donateCasterURL, payload.toString(), Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            balance = Common.getStartWallet(baseUrl, user).getAsJsonObject("data").get("balance").getAsInt();
            DebugLogger.info("{}", balance);
            balanceAfterDonate = balanceAfterDonate - response.getAsJsonObject("data")
                    .get("amount").getAsInt();
            Assertions.assertEquals(balance, balanceAfterDonate);

            /*test donate match*/
            payload = new JsonObject();
            payload.addProperty("format", 1);
            payload.addProperty("type", 1);
            payload.addProperty("star_default", 1000);
            payload.add("detail", new JsonObject());
            payload.addProperty("time_expired", System.currentTimeMillis() + 6220800000L);
            payload.addProperty("suggester_id", 45);
            payload.addProperty("state", MatchConstants.STATE_VOTING);
            payload.addProperty("create_time", System.currentTimeMillis());
            payload.add("team_player", new JsonArray());

            String startMatchURL = new StringBuilder(baseUrl).append("/admin/match/create").toString();
            response = OkHttpUtil.postJson(startMatchURL, payload.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));

            payload = new JsonObject();
            long matchId = response.getAsJsonObject("data").get("id").getAsLong();
            payload.addProperty("targetId", matchId);
            payload.addProperty("star", amount);
            payload.addProperty("message", "Donate match");
            String donateMatchURL = new StringBuilder(baseUrl).append("/donate/match").toString();
            response = OkHttpUtil.postJson(donateMatchURL, payload.toString(), Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            balance = Common.getStartWallet(baseUrl, user).getAsJsonObject("data").get("balance").getAsInt();
            DebugLogger.info("{}", balance);
            balanceAfterDonate = balanceAfterDonate - response.getAsJsonObject("data")
                    .get("amount").getAsInt();
            Assertions.assertEquals(balance, balanceAfterDonate);

            /*test list donate by target*/
            int page = 1;
            String listDonateURL = new StringBuilder(baseUrl).append("/donate/list")
                    .append("?target_id=").append(matchId).append("&page=").append(page).toString();
            response = OkHttpUtil.get(listDonateURL, Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertFalse(response.getAsJsonArray("data").isEmpty());

            /*test list top donate by target*/
            long from = 0L;
            long to = 1792678938104L;
            String listTopDonateURL = new StringBuilder(baseUrl).append("/donate/list-top")
                    .append("?target_id=").append(matchId).append("&from=").append(from)
                    .append("&to=").append(to).append("&page=").append(page).toString();
            response = OkHttpUtil.get(listTopDonateURL, Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertFalse(response.getAsJsonArray("data").isEmpty());

            /*test list top all donate*/
            String listTopAllDonateURL = new StringBuilder(baseUrl).append("/donate/list-top-all")
                    .append("?from=").append(from)
                    .append("&to=").append(to).append("&page=").append(page).toString();
            response = OkHttpUtil.get(listTopAllDonateURL, Common.createHeader(user));
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
