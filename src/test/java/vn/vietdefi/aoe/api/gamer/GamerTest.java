package vn.vietdefi.aoe.api.gamer;

import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class GamerTest {
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
            long id = 105;
            JsonObject response = Common.deleleUser(baseUrl, "0915434544", "password");
            DebugLogger.info("{}", response);
            String deleteGamer = new StringBuilder(baseUrl).append("/gamer/delete").toString();
            JsonObject data = new JsonObject();
            data.addProperty("id",id);
            response = OkHttpUtil.postJson(deleteGamer,data.toString(), Common.createHeaderSystemAdmin());
            DebugLogger.info("{}", response);
            data = new JsonObject();
            data.addProperty("phone","0915434544");
            data.addProperty("nick_name","khanhsof5");
            data.addProperty("full_name","KhanhAd");
            data.addProperty("avatar","http://");
            data.addProperty("detail","{}");
            data.addProperty("clan_id","1");
            data.addProperty("rank",1);
            data.addProperty("rank_info","{'Solo': 'Top1'}");
            data.addProperty("update_time",System.currentTimeMillis());
            String createUrl = new StringBuilder(baseUrl)
                    .append("/gamer/create").toString();
            response = OkHttpUtil.postJson(createUrl, data.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}",response);
            JsonObject gamer = response.getAsJsonObject("data");
            id = gamer.get("user_id").getAsLong();

            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));

            //Get clan
            String getUrlById = new StringBuilder(baseUrl)
                    .append("/gamer/get?id=")
                    .append(id).toString();
            response = OkHttpUtil.get(getUrlById);
            DebugLogger.info("{}", response);
            JsonObject getClan = response.getAsJsonObject("data");
            Assertions.assertEquals(clan.toString(), getClan.toString());

            //Get clan
            int page = 1;
            String getUrlListGamer = new StringBuilder(baseUrl)
                    .append("/gamer/list?page=")
                    .append(page).toString();
            response = OkHttpUtil.postJson(getUrlById, data.toString());
            DebugLogger.info("{}", response);
            getClan = response.getAsJsonObject("data");
            Assertions.assertEquals(clan.toString(), getClan.toString());

            //Xoa luon clan vua tao

        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
