package vn.vietdefi.aoe.api.bank;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.bank.logic.BankAccountState;
import vn.vietdefi.bank.logic.BankCode;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;
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
            username = "0964714430";
            password = "Haikun159";
        }
        @Test
        public void test0() {
            //get working bank
            JsonObject response = getListBank();
            JsonArray banks = response.getAsJsonObject("data").getAsJsonArray("banks");
            if(banks.size() > 0){
                JsonObject bank = banks.get(0).getAsJsonObject();
                long id = bank.get("id").getAsLong();
                disable(id);
                response = getBankSuccess(id);
                bank = response.getAsJsonObject("data");
                Assertions.assertTrue(bank.get("state").getAsInt() == BankAccountState.DISABLE);
                waitToWork(id);
                response = getBankSuccess(id);
                bank = response.getAsJsonObject("data");
                Assertions.assertTrue(bank.get("state").getAsInt() == BankAccountState.WAIT_TO_WORK);
                startWork(id);
                response = getBankSuccess(id);
                bank = response.getAsJsonObject("data");
                Assertions.assertTrue(bank.get("state").getAsInt() == BankAccountState.WORKING);
                response = getOneWorkingBank();
                Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            }
        }
        private JsonObject getBankSuccess(long id) {
            String url = new StringBuilder(baseUrl)
                    .append("/bank/get").toString();
            JsonObject payload = new JsonObject();
            payload.addProperty("id", id);
            JsonObject response =
                    OkHttpUtil.postJson(url, payload.toString(), Common.createHeaderSupperAdmin());
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            return response;
        }

        private void disable(long id) {
            String url = new StringBuilder(baseUrl)
                    .append("/bank/disable").toString();
            JsonObject payload = new JsonObject();
            payload.addProperty("id", id);
            JsonObject response =
                    OkHttpUtil.postJson(url, payload.toString(), Common.createHeaderSupperAdmin());
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }
        private void waitToWork(long id) {
            String url = new StringBuilder(baseUrl)
                    .append("/bank/wait_to_work").toString();
            JsonObject payload = new JsonObject();
            payload.addProperty("id", id);
            JsonObject response =
                    OkHttpUtil.postJson(url, payload.toString(), Common.createHeaderSupperAdmin());
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }
        private void startWork(long id) {
            String url = new StringBuilder(baseUrl)
                    .append("/bank/work").toString();
            JsonObject payload = new JsonObject();
            payload.addProperty("id", id);
            JsonObject response =
                    OkHttpUtil.postJson(url, payload.toString(), Common.createHeaderSupperAdmin());
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }
        private JsonObject getListBank(){
            String url = new StringBuilder(baseUrl)
                    .append("/bank/list?page=1").toString();
            JsonObject response = OkHttpUtil.get(url, Common.createHeaderSystemAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            return response;
        }

        private JsonObject getListBankByState(int state){
            String url = new StringBuilder(baseUrl)
                    .append("/bank/list_by_state?page=1&state=").append(state).toString();
            JsonObject response = OkHttpUtil.get(url, Common.createHeaderSupport());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            return response;
        }
        private JsonObject getOneWorkingBank(){
            String url = new StringBuilder(baseUrl)
                    .append("/bank/get/work").toString();
            JsonObject response = OkHttpUtil.get(url);
            DebugLogger.info("{}", response);
            return response;
        }
        private void loginBankFailed() {
            String url = new StringBuilder(baseUrl)
                    .append("/bank/login").toString();
            JsonObject payload = new JsonObject();
            payload.addProperty("bank_code", BankCode.TIMO);
            payload.addProperty("username", username);
            payload.addProperty("password", "1234");
            JsonObject response =
                    OkHttpUtil.postJson(url, payload.toString(), Common.createHeaderSupperAdmin());
            Assertions.assertTrue(!BaseResponse.isSuccessFullMessage(response));
        }
        private void commitOTP(long id, String otp){
            String url = new StringBuilder(baseUrl)
                    .append("/bank/commit").toString();
            JsonObject payload = new JsonObject();
            payload.addProperty("bank_code", BankCode.TIMO);
            payload.addProperty("id", id);
            payload.addProperty("otp", otp);
            JsonObject response =
                    OkHttpUtil.postJson(url, payload.toString(), Common.createHeaderSupperAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }
        private JsonObject loginBankSuccess() {
            String url = new StringBuilder(baseUrl)
                    .append("/bank/login").toString();
            JsonObject payload = new JsonObject();
            payload.addProperty("bank_code", BankCode.TIMO);
            payload.addProperty("username", username);
            payload.addProperty("password", password);
            JsonObject response =
                    OkHttpUtil.postJson(url, payload.toString(), Common.createHeaderSupperAdmin());
            DebugLogger.info("{}", response);
            return response;
        }
    }


    @BeforeAll
    static void done() {
        DebugLogger.info("AuthTest done");
    }
}
