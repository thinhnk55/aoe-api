package vn.vietdefi.aoe.api.impresario;

import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class ImpresarioTest {
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
            phone = "11111232596";
        }

        @RepeatedTest(1)
        void repeatTest1() {

        }

        @Test
        public void test0() {
            deleteImpresario();
            JsonObject data = createImpresarioSuccess();
            updateImpresario(data);
            getImpresario(data);
            getListImpresario();
            deleteImpresario();
        }
        public JsonObject createImpresarioSuccess() {
            JsonObject impresario = new JsonObject();
            impresario.addProperty("avatar","logo1");
            impresario.addProperty("full_name","full_name1");
            impresario.addProperty("date_of_birth",1200000);
            impresario.addProperty("nationality", "nationality1");
            impresario.addProperty("phone",phone);
            impresario.addProperty("place_of_origin", "Ha Noi");
            String url = new StringBuilder(baseUrl)
                    .append("/impresario/create").toString();
            JsonObject response = OkHttpUtil.postJson(url, impresario.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", url);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            return response.getAsJsonObject("data");
        }
        public void deleteImpresario(){
            String deleteGamer = new StringBuilder(baseUrl).append("/impresario/delete").toString();
            JsonObject payload = new JsonObject();
            payload.addProperty("phone", phone);
            JsonObject response =
                    OkHttpUtil.postJson(deleteGamer, payload.toString(), Common.createHeaderSystemAdmin());
            DebugLogger.info("{}", response);
        }
        public void updateImpresario(JsonObject data){
            long userId = data.get("user_id").getAsLong();
            JsonObject impresario = new JsonObject();
            impresario.addProperty("user_id",userId);
            impresario.addProperty("avatar","logo1");
            impresario.addProperty("full_name","full_name1");
            impresario.addProperty("date_of_birth",1200000);
            impresario.addProperty("nationality", "nationality1");
            impresario.addProperty("phone",phone);
            impresario.addProperty("place_of_origin", "Ha Noi");
            String url = new StringBuilder(baseUrl).append("/impresario/update").toString();
            JsonObject response = OkHttpUtil.postJson(url, impresario.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }
        public void getImpresario(JsonObject data){
            long userId = data.get("user_id").getAsLong();
            String url = new StringBuilder(baseUrl)
                    .append("/impresario/get?user_id=").append(userId).toString();
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.get(url);
            DebugLogger.info("{}",response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject impresario = response.getAsJsonObject("data");
            Assertions.assertEquals(phone, impresario.get("phone").getAsString());
        }
        public void getListImpresario(){
            String url = new StringBuilder(baseUrl)
                    .append("/impresario/list?page=").append(1).toString();
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.get(url);
            DebugLogger.info("{}",response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject data = response.getAsJsonObject("data");
            Assertions.assertTrue(data.getAsJsonArray("impresario").size() > 0);
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
