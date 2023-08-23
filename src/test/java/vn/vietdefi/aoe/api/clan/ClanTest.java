package vn.vietdefi.aoe.api.clan;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.aoe.services.profile.ProfileConstant;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class ClanTest {
    @BeforeAll
    static void init(){
        try {
            DOMConfigurator.configure("config/aoe/log/log4j.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nested
    class ClanTest1{
        String baseUrl;
        String nick_name;

        @BeforeEach
        void init(){
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://127.0.0.1:8000/aoe";
            nick_name = "SBS123";
        }
        @RepeatedTest(1)
        void repeatTest1(){

        }
        @Test
        public void test0(){
            deleteClan();
            JsonObject clan = createClanSuccess();
            createClanFailure();
            getClan(clan.get("id").getAsLong());
            updateClan(clan);
            getListClan();
            deleteClan();
        }

        private void getListClan(){
            JsonObject data = new JsonObject();
            data.addProperty("nick_name", nick_name);
            String url = new StringBuilder(baseUrl)
                    .append("/clan/list?page=1").toString();
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.get(url);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonArray list = response.getAsJsonObject("data").getAsJsonArray("clans");
            Assertions.assertTrue(list.size() > 0);
            DebugLogger.info("{}", response);
        }

        private void updateClan(JsonObject clan) {
            clan.addProperty("nick_name", nick_name);
            clan.addProperty("full_name", "123123");
            clan.addProperty("avatar", "https://chimsssedinang.com/wp-content/uploads/2021/10/clan-sbs-450x600-1.png");
            clan.addProperty("create_day", "1622400000000");
            clan.addProperty("founder", "Nguyen Duc Binh");
            clan.addProperty("owner_unit", "Chim Se Di Nang Studio");
            clan.addProperty("sport", "Age of Empsire");
            JsonObject detail1 = new JsonObject();
            detail1.addProperty("facebook_link", "httssps:/");
            detail1.addProperty("fanpage_link", "httssps:/");
            detail1.addProperty("tiktok_link", "httpsss:/");
            detail1.addProperty("youtube_link", "httssps:/");
            clan.add("detail", detail1);
            String updateClan = new StringBuilder(baseUrl)
                    .append("/clan/update").toString();
            JsonObject response = OkHttpUtil.postJson(updateClan, clan.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }

        private void getClan(long id) {
            JsonObject data = new JsonObject();
            data.addProperty("nick_name", nick_name);
            String createUrl = new StringBuilder(baseUrl)
                    .append("/clan/get?clan_id=").append(id).toString();
            JsonObject response = OkHttpUtil.get(createUrl);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject clan = response.getAsJsonObject("data");
            Assertions.assertTrue(clan.get("id").getAsLong() == id);
            DebugLogger.info("{}", response);
        }

        public void deleteClan(){
            String url = new StringBuilder(baseUrl)
                    .append("/clan/delete").toString();
            JsonObject data = new JsonObject();
            data.addProperty("clan", nick_name);
            JsonObject response = OkHttpUtil.postJson(url, data.toString(), Common.createHeaderSystemAdmin());
            DebugLogger.info("{}", response);
        }
        public void createClanFailure(){
            JsonObject data = new JsonObject();
            data.addProperty("nick_name", nick_name);
            String createUrl = new StringBuilder(baseUrl)
                    .append("/clan/create").toString();
            JsonObject response = OkHttpUtil.postJson(createUrl, data.toString(),Common.createHeaderAdmin());
            Assertions.assertTrue(response.get("error").getAsInt() == 12);
            DebugLogger.info("{}", response);
        }
        public JsonObject createClanSuccess(){
            JsonObject data = new JsonObject();
            data.addProperty("nick_name", nick_name);
            data.addProperty("full_name", "slowbutsure");
            data.addProperty("avatar", "https://chimsedinang.com/wp-content/uploads/2021/10/clan-sbs-450x600-1.png");
            data.addProperty("create_day", "1622400000000");
            data.addProperty("founder", "Nguyen Duc Binh");
            data.addProperty("owner_unit", "Chim Se Di Nang Studio");
            data.addProperty("sport", "Age of Empire");
            JsonObject detail = new JsonObject();
            detail.addProperty("facebook_link", "https:/");
            detail.addProperty("fanpage_link", "https:/");
            detail.addProperty("tiktok_link", "https:/");
            detail.addProperty("youtube_link", "https:/");
            data.add("detail", detail);
            data.addProperty("state", 0);
            String createUrl = new StringBuilder(baseUrl)
                    .append("/clan/create").toString();
            JsonObject response = OkHttpUtil.postJson(createUrl, data.toString(),Common.createHeaderAdmin());
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject clan = response.getAsJsonObject("data");
            Assertions.assertEquals(clan.get("nick_name").getAsString(), nick_name);
            DebugLogger.info("{}", response);
            return clan;
        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
