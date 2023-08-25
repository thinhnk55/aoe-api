package vn.vietdefi.aoe.api.donor;

import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class DonorTest {
    @BeforeAll
    static void init() {
        try {
            DOMConfigurator.configure("config/aoe/log/log4j.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nested
    class ClanTest1 {
        String baseUrl;
        String username;
        String password;
        String phone;
        @BeforeEach
        void init() {
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://127.0.0.1:8000/aoe";
            username = "09732172134";
            password = "123456";
            phone = "1265499596";
        }

        @RepeatedTest(1)
        void repeatTest1() {

        }

        @Test
        public void test0() {
            deleteDonor();
            JsonObject data = createDonorSuccess();
            updateDonor(data);
            getDonor(data);
            getListDonor();
            deleteDonor();
        }
        public JsonObject createDonorSuccess() {
            JsonObject donor = new JsonObject();
            donor.addProperty("logo","logo");
            donor.addProperty("full_name","full_name");
            donor.addProperty("total_donated",1000000);
            donor.add("detail",new JsonObject());
            donor.addProperty("phone",phone);
            String url = new StringBuilder(baseUrl)
                    .append("/donor/create").toString();
            JsonObject response = OkHttpUtil.postJson(url, donor.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", url);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            return response.getAsJsonObject("data");
        }
        public void deleteDonor(){
            String deleteGamer = new StringBuilder(baseUrl).append("/donor/delete").toString();
            JsonObject payload = new JsonObject();
            payload.addProperty("phone", phone);
            JsonObject response =
                    OkHttpUtil.postJson(deleteGamer, payload.toString(), Common.createHeaderSystemAdmin());
            DebugLogger.info("{}", response);
        }
        public void updateDonor(JsonObject data){
            long userId = data.get("user_id").getAsLong();
            JsonObject donor = new JsonObject();
            donor.addProperty("user_id", userId);
            donor.addProperty("logo","logo1");
            donor.addProperty("full_name","full_name1");
            donor.addProperty("total_donated",1200000);
            donor.add("detail",new JsonObject());
            donor.addProperty("phone",phone);
            String url = new StringBuilder(baseUrl).append("/donor/update").toString();
            JsonObject response = OkHttpUtil.postJson(url, donor.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }
        public void getDonor(JsonObject data){
            long userId = data.get("user_id").getAsLong();
            String url = new StringBuilder(baseUrl)
                    .append("/donor/get?user_id=").append(userId).toString();
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.get(url);
            DebugLogger.info("{}",response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject donor = response.getAsJsonObject("data");
            Assertions.assertEquals(phone, donor.get("phone").getAsString());
        }
        public void getListDonor(){
            String url = new StringBuilder(baseUrl)
                    .append("/donor/list?page=").append(1).toString();
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.get(url);
            DebugLogger.info("{}",response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject data = response.getAsJsonObject("data");
            Assertions.assertTrue(data.getAsJsonArray("donor").size() > 0);
        }
        @Test
        public void test1() {
        }
    }

    @BeforeAll
    static void done() {
        DebugLogger.info("AuthTest done");
    }
}
