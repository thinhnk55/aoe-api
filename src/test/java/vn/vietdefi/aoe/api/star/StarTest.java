package vn.vietdefi.aoe.api.star;

import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.aoe.services.star.StarConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class StarTest {
    @BeforeAll
    static void init(){
        try {
            DOMConfigurator.configure("config/aoe/log/log4j.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nested
    class StarTest1{
        String baseUrl;
        String username;
        String password;

        @BeforeEach
        void init(){
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://127.0.0.1:8000/aoe";
//            baseUrl = "http://192.168.1.99:8000/aoe";
            username = "086888555";
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

            /*test get star wallet*/
            String getStartURL = new StringBuilder(baseUrl).append("/star/get").toString();
            DebugLogger.info("{}", getStartURL);
            response = OkHttpUtil.get(getStartURL, Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));

            JsonObject payload = new JsonObject();
            payload.addProperty("user_id", user.get("id").getAsLong());
            int service = StarConstant.SERVICE_DONATE_GAMER;
            payload.addProperty("service", service);
            payload.addProperty("amount", 1000);
            payload.addProperty("referId", 0);
            String adminExchangeStar = new StringBuilder(baseUrl).append("/star/admin/exchange").toString();
            DebugLogger.info("{}", adminExchangeStar);
            response = OkHttpUtil.postJson(adminExchangeStar, payload.toString(), Common.createHeaderSystemAdmin());
            JsonObject transaction = response.getAsJsonObject("data");
            DebugLogger.info("{}", transaction);
            OkHttpUtil.postJson(adminExchangeStar, payload.toString(), Common.createHeaderSystemAdmin());
            OkHttpUtil.postJson(adminExchangeStar, payload.toString(), Common.createHeaderSystemAdmin());
            OkHttpUtil.postJson(adminExchangeStar, payload.toString(), Common.createHeaderSystemAdmin());
            OkHttpUtil.postJson(adminExchangeStar, payload.toString(), Common.createHeaderSystemAdmin());
            OkHttpUtil.postJson(adminExchangeStar, payload.toString(), Common.createHeaderSystemAdmin());
            DebugLogger.info("{}", response);

            long from = 1592373925745L;
            long to = 1792373925745L;
            int page = 1;
            String getTransactionByTimeURL = new StringBuilder(baseUrl).append("/star/transaction/time")
                    .append("?from=").append(from).append("&to=").append(to).append("&page=").append(page).toString();
            DebugLogger.info("{}", getTransactionByTimeURL);
            response = OkHttpUtil.get(getTransactionByTimeURL, Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertTrue(response.get("data").getAsJsonArray().size() == 6);

            from = 0;
            to = 1792373925745L;
            page = 1;
            getTransactionByTimeURL = new StringBuilder(baseUrl).append("/star/transaction/time")
                    .append("?from=").append(from).append("&to=").append(to).append("&page=").append(page).toString();
            DebugLogger.info("{}", getTransactionByTimeURL);
            response = OkHttpUtil.get(getTransactionByTimeURL, Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertTrue(response.get("data").getAsJsonArray().size() == 6);

            String getTransactionByServiceURL = new StringBuilder(baseUrl).append("/star/transaction/service")
                    .append("?service=").append(StarConstant.SERVICE_DONATE_GAMER).toString();
            DebugLogger.info("{}", getTransactionByServiceURL);
            response = OkHttpUtil.get(getTransactionByServiceURL, Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertTrue(response.get("data").getAsJsonArray().size() == 6);


            long transactionId = transaction.get("id").getAsLong();
            String getTransactionByIdURL = new StringBuilder(baseUrl).append("/star/transaction/get")
                    .append("?id=").append(transactionId).toString();
            DebugLogger.info("{}", getTransactionByIdURL);
            response = OkHttpUtil.get(getTransactionByIdURL, Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertNotNull(response.getAsJsonObject("data"));
            Assertions.assertTrue(response.getAsJsonObject("data").get("id").getAsLong() == transactionId);

            /*lookup recharge transaction*/
            String lookupRechargeURL = new StringBuilder(baseUrl).append("/star/admin/transaction/recharge")
                    .append("?from=").append("1692349077941").append("&to=").append("1692960355418")
                    .append("&page=").append(1).toString();
            DebugLogger.info("{}", lookupRechargeURL);
            response = OkHttpUtil.get(lookupRechargeURL, Common.createHeaderSupport());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertEquals(20, response.getAsJsonObject("data").getAsJsonArray("transaction").size());

            String adminGetTransactionByTimeURL = new StringBuilder(baseUrl).append("/star/admin/transaction/time")
                    .append("?user_id=").append("6").append("&from=").append("1692349920758").append("&to=").append("1692677189120")
                    .append("&page=").append(1).toString();
            DebugLogger.info("{}", adminGetTransactionByTimeURL);
            response = OkHttpUtil.get(adminGetTransactionByTimeURL, Common.createHeaderSupperAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));

            /*
            get list star transaction refund donate
             */
            String getListStarRefundURL = new StringBuilder(baseUrl).append("/star/refund/list")
                    .toString();
            DebugLogger.info("{}", getListStarRefundURL);
            response = OkHttpUtil.get(getListStarRefundURL, Common.createHeaderSupport());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertTrue(response.get("data").getAsJsonArray().size() > 0);

            /*test admin get star wallet by userId*/
            long starWalletId = user.get("id").getAsLong();
            String adminGetStarURL = new StringBuilder(baseUrl).append("/star/admin/get")
                    .append("?user_id=").append(1).toString();
            DebugLogger.info("{}", adminGetStarURL);
            response = OkHttpUtil.get(adminGetStarURL, Common.createHeaderSupport());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));

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
