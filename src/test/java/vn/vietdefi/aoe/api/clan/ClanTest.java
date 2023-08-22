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
        String clan;
        String username;
        String password;

        @BeforeEach
        void init(){
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://127.0.0.1:8000/aoe";
            clan = "SBS";
        }
        @RepeatedTest(1)
        void repeatTest1(){

        }
        @Test
        public void test0(){

        }
        @Test
        public void test1(){
            //Tao Clan
            JsonObject data = new JsonObject();
            data.addProperty("nick_name", "SBSSSDss");
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

            String createUrl = new StringBuilder(baseUrl)
                    .append("/clan/create").toString();
            JsonObject response = OkHttpUtil.postJson(createUrl, data.toString(),Common.createHeaderAdmin());
            JsonObject clan = response.getAsJsonObject("data");
            String clanNickName = clan.get("nick_name").getAsString();
            long clanId = clan.get("id").getAsLong();
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            //Get clan
            String getUrlByName = new StringBuilder(baseUrl)
                    .append("/clan/get-by-name?clan_name=")
                    .append(clanNickName).toString();
            response = OkHttpUtil.get(getUrlByName);
            DebugLogger.info("{}", response);
            JsonObject getClan = response.getAsJsonObject("data");
            long clanIdNew = getClan.get("id").getAsLong();
            Assertions.assertEquals(clanId, clanIdNew);

            //Get clan
            String getUrlById = new StringBuilder(baseUrl)
                    .append("/clan/get?clan_id=")
                    .append(clanId).toString();
            response = OkHttpUtil.get(getUrlById);
            DebugLogger.info("{}", response);
            getClan = response.getAsJsonObject("data");
            clanIdNew = getClan.get("id").getAsLong();
            Assertions.assertEquals(clanId, clanIdNew);
            //update clan
            JsonObject data1 = new JsonObject();
            data1.addProperty("id", clanId);
            data1.addProperty("nick_name", "SBSDDDSSD3");
            data1.addProperty("full_name", "slowbutsssure");
            data1.addProperty("avatar", "https://chimsssedinang.com/wp-content/uploads/2021/10/clan-sbs-450x600-1.png");
            data1.addProperty("create_day", "1622400000000");
            data1.addProperty("founder", "Nguyen Duc Binh");
            data1.addProperty("owner_unit", "Chim Se Di Nang Studio");
            data1.addProperty("sport", "Age of Empsire");
            JsonObject detail1 = new JsonObject();
            detail1.addProperty("facebook_link", "httssps:/");
            detail1.addProperty("fanpage_link", "httssps:/");
            detail1.addProperty("tiktok_link", "httpsss:/");
            detail1.addProperty("youtube_link", "httssps:/");
            data1.add("detail", detail1);;
            String updateClan = new StringBuilder(baseUrl)
                    .append("/clan/update").toString();
            response = OkHttpUtil.postJson(updateClan, data1.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            //list clan
            String getList = new StringBuilder(baseUrl)
                    .append("/clan/list").toString();
            response = OkHttpUtil.get(getList);
            DebugLogger.info("{}", response);
            JsonArray clans = response.getAsJsonObject("data").getAsJsonArray("clans");
            int size = clans.size();
            Assertions.assertTrue(size > 0);
            //Xoa luon clan vua tao
            String deleteUrl = new StringBuilder(baseUrl)
                    .append("/clan/delete").toString();
            data = new JsonObject();
            data.addProperty("clan_id", clanId);
            response = OkHttpUtil.postJson(deleteUrl, data.toString(), Common.createHeaderSystemAdmin());
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            DebugLogger.info("{}", response);
        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
