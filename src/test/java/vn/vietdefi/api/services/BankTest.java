package vn.vietdefi.api.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.aoe.services.bank.AoeBankHandlerService;
import vn.vietdefi.api.services.auth.UserConstant;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.bank.logic.BankAccountState;
import vn.vietdefi.bank.logic.BankController;
import vn.vietdefi.bank.services.BankService;
import vn.vietdefi.bank.services.IBankHandlerService;
import vn.vietdefi.bank.services.IBankService;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.string.StringUtil;

import java.util.Random;
import java.util.jar.JarEntry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankTest {
    @BeforeAll
    static void init() {
        try {
            DOMConfigurator.configure("config/aoe/log/log4j.xml");
            HikariClients.instance().init("config/aoe/sql/databases.json"
                    , "config/aoe/sql/hikari.properties");
            ApiConfig.instance().init("config/aoe/http/http.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nested
    class BankTest1 {
        String baseUrl;
        String username;
        String password;
        IBankService bankService;


        @BeforeEach
        void init() {
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://127.0.0.1:8000/aoe";
            username = "0915434549";
            password = "KhanhAk54";
            bankService = new BankService();
        }

//        @Test
//        public void test0() {
//            JsonObject response = AoeServices.clanService.getClanById(50);
//            DebugLogger.info("{}", response);
//            JsonObject clan = response.getAsJsonObject("data");
//            String random = StringUtil.randomString(8);
//            clan.addProperty("nick_name", random);
//            response = AoeServices.clanService.updateClan(clan);
//            DebugLogger.info("{}", response);
//        }

//        @Test
//        public void test1() {
//            JsonObject data = new JsonObject();
//            String random = StringUtil.randomString(8);
//            data.addProperty("nick_name", random);
//            data.addProperty("full_name", "slowbutsure");
//            data.addProperty("avatar", "https://chimsedinang.com/wp-content/uploads/2021/10/clan-sbs-450x600-1.png");
//            data.addProperty("create_day", 1622400000000L);
//            data.addProperty("founder", "Nguyen Duc Binh");
//            data.addProperty("owner_unit", "Chim Se Di Nang Studio");
//            data.addProperty("sport", "Age of Empire");
//            JsonObject detail = new JsonObject();
//            detail.addProperty("facebook_link", "https:/");
//            detail.addProperty("fanpage_link", "https:/");
//            detail.addProperty("tiktok_link", "https:/");
//            detail.addProperty("youtube_link", "https:/");
//            data.add("detail", detail);
//            data.addProperty("state", 0);
//            JsonObject response = AoeServices.clanService.createClan(data);
//            DebugLogger.info("{}", response);
//            JsonObject clan = response.getAsJsonObject("data");
//            DebugLogger.info("{}", clan);
//            long clanId = clan.get("id").getAsLong();
//            response = AoeServices.clanService.getClanById(clanId);
//            DebugLogger.info("{}", response);
//            clan = response.getAsJsonObject("data");
//            DebugLogger.info("{}", clan);
//            DebugLogger.info("{}", data);
//
////            clan.addProperty("nick_name", "HUHUHAHA");
////            clan.addProperty("full_name", "123123");
////            clan.addProperty("avatar", "https://chimsssedinang.com/wp-content/uploads/2021/10/clan-sbs-450x600-1.png");
////            clan.addProperty("create_day", "1622400000000");
////            clan.addProperty("founder", "Nguyen Duc Binh");
////            clan.addProperty("owner_unit", "Chim Se Di Nang Studio");
////            clan.addProperty("sport", "Age of Empsire");
////            JsonObject detail1 = new JsonObject();
////            detail1.addProperty("facebook_link", "httssps:/");
////            detail1.addProperty("fanpage_link", "httssps:/");
////            detail1.addProperty("tiktok_link", "httpsss:/");
////            detail1.addProperty("youtube_link", "httssps:/");
////            clan.add("detail", detail1);
////            DebugLogger.info("{}", clan);
//
//            response = AoeServices.clanService.updateClan(data);
//            DebugLogger.info("{}", response);
//        }

        @Test
        public void test0() {
            JsonObject data = TestGetListBank();
            TestSelectBank(data);
        }

        @Test
        public JsonObject TestGetListBank() {
            String url = new StringBuilder(baseUrl)
                    .append("/bank/list").toString();
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.get(url, Common.createHeaderSystemAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonArray data = response.getAsJsonArray("data");
            Assertions.assertTrue(data.size() > 0);
            return response;
        }

        @Test
        public void TestUpdateBank(JsonObject data) {


        }

        @Test
        public void TestSelectBank(JsonObject data) {
            String url = new StringBuilder(baseUrl)
                    .append("/bank/select").toString();
            DebugLogger.info("{}", url);
            JsonObject response = new JsonObject();
            JsonArray bank = data.getAsJsonArray("data");
            JsonArray bankSelect = new JsonArray();
            for (int i = 0; i < bank.size(); i++) {
                int state = bank.get(i).getAsJsonObject().get("state").getAsInt();
                if (state == BankAccountState.ACTIVE) {
                    bankSelect.add(bank.get(i));
                }
            }
            if (!bankSelect.isEmpty()) {
                // select random bankActive
                Random random = new Random();
                JsonObject Selected = (JsonObject) bankSelect.get(random.nextInt(bank.size()));
                DebugLogger.info("Selected Bank {}", Selected);
                response = OkHttpUtil.postJson(url, Selected.toString(), Common.createHeaderSystemAdmin());
                DebugLogger.info("Response select {}", response);
                Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
                bankService.updateBankAccountState(Selected.get("id").getAsInt(), 2);
            } else {
                DebugLogger.info("Not Selected Bank {}");
            }
        }
    }


    @BeforeAll
    static void done() {
        DebugLogger.info("AuthTest done");
    }
}
