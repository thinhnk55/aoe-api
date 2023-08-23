package vn.vietdefi.aoe.api.caster;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class CasterTest {
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
        String nick_name;
        String username;

        @BeforeEach
        void init() {
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://127.0.0.1:8000/aoe";
            username = "09846673112";
            nick_name = "SBS2804";
        }

        @RepeatedTest(1)
        void repeatTest1() {

        }

        @Test
        public void test0() {
            deleteCaster();
            JsonObject caster = createCasterSuccess();
            createCasterFailure();
            getCaster(caster.get("user_id").getAsLong());
            updateCaster(caster);
            getListCaster();
            deleteCaster();
        }
        public void deleteCaster(){
            String deleteUrl = new StringBuilder(baseUrl)
                    .append("/caster/delete").toString();
            JsonObject data = new JsonObject();
            data.addProperty("nick_name", nick_name);
            JsonObject response = OkHttpUtil.postJson(deleteUrl, data.toString(), Common.createHeaderSystemAdmin());
            DebugLogger.info("{}", response);
        }
        public void updateCaster(JsonObject caster){
            caster.addProperty("phone", username);
            caster.addProperty("nick_name", nick_name);
            caster.addProperty("full_name", "Nguyen Duc Binh HAIII");
            caster.addProperty("avatar", "https://chimsedinang.com/wp-content/uploads/2021/10/clan-sbs-450x600-1.png");
            caster.addProperty("clan_id", 1);
            JsonObject detail1 = new JsonObject();
            detail1.addProperty("facebook_link", "httpsss:/");
            detail1.addProperty("fanpage_link", "httpsss:/");
            detail1.addProperty("tiktok_link", "httpsss:/");
            detail1.addProperty("youtube_link", "httpsss:/");
            caster.add("detail", detail1);
            String updateUrl = new StringBuilder(baseUrl)
                    .append("/caster/update")
                    .toString();
            JsonObject response = OkHttpUtil.postJson(updateUrl, caster.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }
        public void getListCaster() {
            JsonObject data = new JsonObject();
            data.addProperty("nick_name", nick_name);
            String url = new StringBuilder(baseUrl)
                    .append("/caster/list?page=1").toString();
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.get(url);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonArray list = response.getAsJsonObject("data").getAsJsonArray("casters");
            Assertions.assertTrue(list.size() > 0);
            DebugLogger.info("{}", response);
        }

        public void getCaster(long id) {
            String createUrl = new StringBuilder(baseUrl)
                    .append("/caster/get?caster_id=").append(id).toString();
            JsonObject response = OkHttpUtil.get(createUrl);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject clan = response.getAsJsonObject("data");
            Assertions.assertTrue(clan.get("user_id").getAsLong() == id);
            DebugLogger.info("{}", response);
        }

        public void createCasterFailure() {
            JsonObject data = new JsonObject();
            data.addProperty("nick_name", nick_name);
            String createUrl = new StringBuilder(baseUrl)
                    .append("/caster/create").toString();
            JsonObject response = OkHttpUtil.postJson(createUrl, data.toString(),Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(response.get("error").getAsInt() == 1);
        }

        public JsonObject createCasterSuccess() {
            JsonObject data = new JsonObject();
            data.addProperty("phone", username);
            data.addProperty("nick_name", nick_name);
            data.addProperty("full_name", "Nguyen Duc Binh");
            data.addProperty("avatar", "https://chimsedinang.com/wp-content/uploads/2021/10/clan-sbs-450x600-1.png");
            data.addProperty("clan_id", 1);
            JsonObject detail = new JsonObject();
            detail.addProperty("facebook_link", "https:/");
            detail.addProperty("fanpage_link", "https:/");
            detail.addProperty("tiktok_link", "https:/");
            detail.addProperty("youtube_link", "https:/");
            data.add("detail", detail);
            String createUrl = new StringBuilder(baseUrl)
                    .append("/caster/create").toString();
            JsonObject response = OkHttpUtil.postJson(createUrl, data.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject caster = response.getAsJsonObject("data");
            Assertions.assertEquals(caster.get("nick_name").getAsString(), nick_name);
            return caster;
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
