package vn.vietdefi.aoe.api.clan;

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

        @BeforeEach
        void init(){
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://192.168.1.99:8000/aoe";
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
            response = OkHttpUtil.postJson(getUrlByName, data.toString());
            DebugLogger.info("{}", response);
            JsonObject getClan = response.getAsJsonObject("data");
            Assertions.assertEquals(clan.toString(), getClan.toString());

            //Get clan
            String getUrlById = new StringBuilder(baseUrl)
                    .append("/clan/get?clan_id=")
                    .append(clanNickName).toString();
            response = OkHttpUtil.postJson(getUrlById, data.toString());
            DebugLogger.info("{}", response);
            getClan = response.getAsJsonObject("data");
            Assertions.assertEquals(clan.toString(), getClan.toString());


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
