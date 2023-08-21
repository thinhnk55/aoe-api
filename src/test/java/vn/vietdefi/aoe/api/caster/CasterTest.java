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
        String phone;

        @BeforeEach
        void init() {
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://127.0.0.1:8000/aoe";
            phone = "0924782311";
        }

        @RepeatedTest(1)
        void repeatTest1() {

        }

        @Test
        public void test0() {

        }

        @Test
        public void test1() {
            //Create caster
            JsonObject data = new JsonObject();
            data.addProperty("phone", phone);
            data.addProperty("nick_name", "BLV HO VAN HON");
            data.addProperty("full_name", "Nguyễn Đức Bình");
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
            //Get caster
            JsonObject caster = response.getAsJsonObject("data" );
            long userId = caster.get("user_id").getAsLong();
            String getUrl = new StringBuilder(baseUrl)
                    .append("/caster/get?caster_id=")
                    .append(userId).toString();
            response = OkHttpUtil.get(getUrl);
            DebugLogger.info("{}", response);
            String phoneAssert = response.getAsJsonObject("data").get("phone").getAsString();
            Assertions.assertEquals(phone, phoneAssert);
            //Update caster
            JsonObject data1 = new JsonObject();
            data1.addProperty("user_id", userId);
            data1.addProperty("phone", phone);
            data1.addProperty("nick_name", "BLV KHANH DUY");
            data1.addProperty("full_name", "Nguyễn Đức Bình H");
            data1.addProperty("avatar", "https://chimsedinang.com/wp-content/uploads/2021/10/clan-sbs-450x600-1.png");
            data1.addProperty("clan_id", 1);
            JsonObject detail1 = new JsonObject();
            detail1.addProperty("facebook_link", "httpsss:/");
            detail1.addProperty("fanpage_link", "httpsss:/");
            detail1.addProperty("tiktok_link", "httpsss:/");
            detail1.addProperty("youtube_link", "httpsss:/");
            data1.add("detail", detail);
            String updateUrl = new StringBuilder(baseUrl)
                    .append("/caster/update")
                    .toString();
            response = OkHttpUtil.postJson(updateUrl, data1.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            //Get list gamer
            String getListGamerUrl = new StringBuilder(baseUrl)
                    .append("/clan/list-gamer-of-clan?clan_id=").append(1).toString();
            response = OkHttpUtil.get(getListGamerUrl);
            DebugLogger.info("{}", response);
            JsonArray gamers = response.getAsJsonObject("data").getAsJsonArray("gamers");
            Assertions.assertTrue(gamers.size() > 0);
            //Delete caster
            String deleteUrl = new StringBuilder(baseUrl)
                    .append("/caster/delete").toString();
            data = new JsonObject();
            data.addProperty("caster_id", userId);
            response = OkHttpUtil.postJson(deleteUrl, data.toString(), Common.createHeaderSystemAdmin());
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            DebugLogger.info("{}", response);
        }
    }

    @BeforeAll
    static void done() {
        DebugLogger.info("AuthTest done");
    }
}
