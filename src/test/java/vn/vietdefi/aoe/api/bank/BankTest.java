package vn.vietdefi.aoe.api.bank;

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nested
    class BankTest1 {
        String baseUrl;
        String username;
        String password;


        @BeforeEach
        void init() {
            baseUrl = "http://127.0.0.1:8000/aoe";
            username = "0915434549";
            password = "KhanhAk54";
        }
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
