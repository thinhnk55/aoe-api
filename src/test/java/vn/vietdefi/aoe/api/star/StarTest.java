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
    class AoeAuthTest1{
        String baseUrl;
        String username;
        String password;

        @BeforeEach
        void init(){
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://192.168.1.18:8000/aoe";
            username = "086888444";
            password = "12344321";
        }
        @RepeatedTest(1)
        void repeatTest1(){
            JsonObject response = Common.deleleUser(baseUrl, username, password);
            DebugLogger.info("{}", response);
            response = Common.registerUserSuccess(baseUrl, username, password);
            DebugLogger.info("{}", response);
            JsonObject user = response.getAsJsonObject("data");
            Assertions.assertNotNull(user.get("star").getAsJsonObject());

            /*test get star wallet*/
            String getStartURL = new StringBuilder(baseUrl).append("/star/get").toString();
            response = OkHttpUtil.get(getStartURL, Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertEquals(user.get("star").getAsJsonObject(), response.getAsJsonObject("data"));

            /*test get by time*/
            long userId = 1;
            String token = "2gbpnlvqtidiifohxnqb1thw1un969uq";
            long from = 1692373548373L;
            long to = 1692373925745L;
            int page = 1;
            String getTransactionByTimeURL = new StringBuilder(baseUrl).append("/star/transaction-by-time")
                    .append("?from=").append(from).append("&to=").append(to).append("&page=").append(page).toString();
            response = OkHttpUtil.get(getTransactionByTimeURL, Common.createHeader(userId, token));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertTrue(response.get("data").getAsJsonArray().size() == 11);

            /*test get star transaction by id*/
            long transactionId = 20;
            String getTransactionByIdURL = new StringBuilder(baseUrl).append("/star/transaction")
                    .append("?id=").append(transactionId).toString();
            response = OkHttpUtil.get(getTransactionByIdURL, Common.createHeader(user));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertNotNull(response.getAsJsonObject("data"));
            Assertions.assertTrue(response.getAsJsonObject("data").get("id").getAsLong() == transactionId);

            /*test admin get star wallet by userId*/
            long starWalletId = 13;
            String adminGetStarURL = new StringBuilder(baseUrl).append("/star/admin/get")
                    .append("?id=").append(starWalletId).toString();
            response = OkHttpUtil.get(adminGetStarURL, Common.createHeader(Common.support_id, Common.support_token));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            Assertions.assertEquals(response.getAsJsonObject("data").get("user_id").getAsLong(), starWalletId);

        }
        @Test
        public void test0(){

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
