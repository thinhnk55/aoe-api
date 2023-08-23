package vn.vietdefi.aoe.api.bank;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.aoe.api.caster.CasterUtil;
import vn.vietdefi.aoe.api.gamer.GamerUtil;
import vn.vietdefi.aoe.api.match.MatchUtil;
import vn.vietdefi.bank.logic.BankCode;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;
import vn.vietdefi.util.random.RandomUtil;

public class BankDonateTest {
    @BeforeAll
    static void init() {
        try {
            DOMConfigurator.configure("config/aoe/log/log4j.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nested
    class BankDonateTest1 {
        String baseUrl;
        String username = "0836993400";


        @BeforeEach
        void init() {
            baseUrl = "http://127.0.0.1:8000/aoe";
        }
        @Test
        public void test0() {
            fixTransaction();
        }
        private void fixTransaction(){
            String url = new StringBuilder(baseUrl).append("/bank/transaction/error").toString();
            JsonObject response = OkHttpUtil.get(url, Common.createHeaderSupport());
            DebugLogger.info("{}", response);
            JsonArray array = response.getAsJsonObject("data").getAsJsonArray("transaction");
            if(array.size() > 0){
                JsonObject transaction = array.get(0).getAsJsonObject();
                transaction.addProperty("note", "0836993400 donate bl1");
                url = new StringBuilder(baseUrl).append("/bank/transaction/fix").toString();
                response = OkHttpUtil.postJson(url, transaction.toString(), Common.createHeaderSupperAdmin());
                DebugLogger.info("{}", response);
            }
        }
        private void donateGamer(){
            JsonObject response = GamerUtil.getListGamer(baseUrl);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonArray data = response.getAsJsonObject("data").getAsJsonArray("gamer");
            if(data.size() > 0) {
                JsonObject gamer = data.get(0).getAsJsonObject();
                long id = gamer.get("user_id").getAsLong();
                DebugLogger.info("{}", response);
                String note = new StringBuilder(username).append(" donate ").append("gt").append(id).toString();
                JsonObject transaction = new JsonObject();
                int random = RandomUtil.nextInt(1000000000);
                String bank_transaction_id = new StringBuilder("TF").append(random).toString();
                transaction.addProperty("bank_transaction_id", bank_transaction_id);
                transaction.addProperty("receiver_bankcode", BankCode.TIMO);
                transaction.addProperty("receiver_bank_account", 900);
                transaction.addProperty("receiver_name", "fake_account");
                transaction.addProperty("note", note);
                transaction.addProperty("amount", 1000000);
                transaction.addProperty("sender_bankcode", -1);
                transaction.addProperty("sender_bank_account", "");
                transaction.addProperty("sender_name", "");
                String url = new StringBuilder(baseUrl).append("/bank/transaction/create").toString();
                response = OkHttpUtil.postJson(url, transaction.toString(), Common.createHeaderSystemAdmin());
                DebugLogger.info("{}", response);
            }
        }
        private void donateCaster(){
            JsonObject response = CasterUtil.getListCaster(baseUrl);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonArray data = response.getAsJsonObject("data").getAsJsonArray("caster");
            if(data.size() > 0) {
                JsonObject caster = data.get(0).getAsJsonObject();
                long id = caster.get("user_id").getAsLong();
                DebugLogger.info("{}", response);
                String note = new StringBuilder(username).append(" donate ").append("bl").append(id).toString();
                JsonObject transaction = new JsonObject();
                int random = RandomUtil.nextInt(1000000000);
                String bank_transaction_id = new StringBuilder("TF").append(random).toString();
                transaction.addProperty("bank_transaction_id", bank_transaction_id);
                transaction.addProperty("receiver_bankcode", BankCode.TIMO);
                transaction.addProperty("receiver_bank_account", 900);
                transaction.addProperty("receiver_name", "fake_account");
                transaction.addProperty("note", note);
                transaction.addProperty("amount", 1000000);
                transaction.addProperty("sender_bankcode", -1);
                transaction.addProperty("sender_bank_account", "");
                transaction.addProperty("sender_name", "");
                String url = new StringBuilder(baseUrl).append("/bank/transaction/create").toString();
                response = OkHttpUtil.postJson(url, transaction.toString(), Common.createHeaderSystemAdmin());
                DebugLogger.info("{}", response);
            }
        }

        private void donateMatch(){
            JsonObject response = MatchUtil.getVotingMatch(baseUrl);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonArray data = response.getAsJsonObject("data").getAsJsonArray("match");
            if(data.size() > 0) {
                JsonObject match = data.get(0).getAsJsonObject();
                long id = match.get("id").getAsLong();
                DebugLogger.info("{}", response);
                String note = new StringBuilder(username).append(" donate ").append("kd").append(id).toString();
                JsonObject transaction = new JsonObject();
                int random = RandomUtil.nextInt(1000000000);
                String bank_transaction_id = new StringBuilder("TF").append(random).toString();
                transaction.addProperty("bank_transaction_id", bank_transaction_id);
                transaction.addProperty("receiver_bankcode", BankCode.TIMO);
                transaction.addProperty("receiver_bank_account", 900);
                transaction.addProperty("receiver_name", "fake_account");
                transaction.addProperty("note", note);
                transaction.addProperty("amount", 1000000);
                transaction.addProperty("sender_bankcode", -1);
                transaction.addProperty("sender_bank_account", "");
                transaction.addProperty("sender_name", "");
                String url = new StringBuilder(baseUrl).append("/bank/transaction/create").toString();
                response = OkHttpUtil.postJson(url, transaction.toString(), Common.createHeaderSystemAdmin());
                DebugLogger.info("{}", response);
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
    }


    @BeforeAll
    static void done() {
        DebugLogger.info("AuthTest done");
    }
}
