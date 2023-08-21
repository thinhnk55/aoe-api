package vn.vietdefi.aoe.api.gamer;

import com.google.gson.JsonArray;
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
        String username;
        String tokenAdminSystem;

        @BeforeEach
        void init(){
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://127.0.0.1:8000/aoe";
            username = "0915434541";
            tokenAdminSystem = "zlmnyk66fi0lhgkr7ol4sqld27xsg1ip";
        }
        @RepeatedTest(1)
        void repeatTest1(){

        }
        @Test
        public void test0(){

        }
        @Test
        public void test1(){
            JsonObject response = GamerDataTest.deleteGamer(baseUrl, username);
            DebugLogger.info("{}", response);
            JsonObject data = new JsonObject();
            //Tao gamer
            //create body
            data.addProperty("phone","0915434541");
            data.addProperty("nick_name","Sbuu");
            data.addProperty("full_name","KhanhAd");
            data.addProperty("avatar","http://");
            JsonObject detail = new JsonObject();
            detail.addProperty("sport"," Age of Empires");
            detail.addProperty("address","NQ-NB");
            detail.addProperty("date_of_birth","07/09/2002");
            detail.add("image",new JsonArray());
            detail.addProperty("nationality","China");
            detail.addProperty("facebook_link","https://docs.google.com/document/d/1td7LtgMVFoL5xrtJ6m0zErOIYDJgBUX2Dlk-vsMpFHU/edit#heading=h.mcb4gmk6o7pq");
            detail.addProperty("telegram_link","https://docs.google.com/document/d/1td7LtgMVFoL5xrtJ6m0zErOIYDJgBUX2Dlk-vsMpFHU/edit#heading=h.mcb4gmk6o7pq");
            detail.addProperty("tiktok_link","https://docs.google.com/document/d/1td7LtgMVFoL5xrtJ6m0zErOIYDJgBUX2Dlk-vsMpFHU/edit#heading=h.mcb4gmk6o7pq");
            detail.addProperty("youtube_link","https://docs.google.com/document/d/1td7LtgMVFoL5xrtJ6m0zErOIYDJgBUX2Dlk-vsMpFHU/edit#heading=h.mcb4gmk6o7pq");
            data.add("detail",detail);
            data.add("rank_info",new JsonObject());
            data.addProperty("clan_id","1");
            data.addProperty("rank",1);
            String createUrl = new StringBuilder(baseUrl)
                    .append("/gamer/create").toString();
            response = OkHttpUtil.postJson(createUrl, data.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}",response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));

            JsonObject gamer = response.getAsJsonObject("data");
            long id = gamer.get("user_id").getAsLong();
            long getClanId = response.getAsJsonObject("data").get("clan_id").getAsLong();
            //Get gamer by id
            String getUrlById = new StringBuilder(baseUrl)
                    .append("/gamer/get?id=")
                    .append(id).toString();
            response = OkHttpUtil.get(getUrlById);
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));

            String updateGamer = new StringBuilder(baseUrl)
                    .append("/gamer/update").toString();
            data =new JsonObject();
            data.addProperty("user_id",id);
            data.addProperty("nick_name", "1");
            data.addProperty("full_name", "1");
            data.addProperty("avatar", "1");
            data.add("detail", new JsonObject());
            data.addProperty("clan_id", "2");
            data.addProperty("rank", "1");
            data.add("rank_info",new JsonObject());
            data.addProperty("state", "0");


            response = OkHttpUtil.postJson(updateGamer,data.toString(),Common.createHeaderSystemAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));



            String getUrlListGamerByClanId = new StringBuilder(baseUrl)
                    .append("/gamer/list-of-clan?clan_id=").append(getClanId).toString();
            response = OkHttpUtil.get(getUrlListGamerByClanId);
            DebugLogger.info("{}",response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));

            //Get list of gamer
            int page = 1;
            String getUrlListGamer = new StringBuilder(baseUrl)
                    .append("/gamer/list?page=")
                    .append(page).toString();
            response = OkHttpUtil.get(getUrlListGamer);
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));


            //Xoa luon gamer vua tao
            response = GamerDataTest.deleteGamer(baseUrl, username);
            DebugLogger.info("{}",response);

        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
