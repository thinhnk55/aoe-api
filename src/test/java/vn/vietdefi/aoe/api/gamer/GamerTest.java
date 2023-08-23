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
    class GamerTest1{
        String baseUrl;
        long userId;
        String username;
        String nickname;
        long clanId;
        long clanIdUpdate;

        @BeforeEach
        void init(){
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://127.0.0.1:8000/aoe";
            username = "0988666555";
            nickname = "gamer_nick_name";
            clanId = 1;
            clanIdUpdate = 3;
        }
        @RepeatedTest(1)
        void repeatTest1(){

        }

        @Test
        public void test0(){
            deleteGamer();
            JsonObject data = createGamerSuccess();
            createGamerError();
            updateGamer(data);
            getGamer(data);
            getListGamer();
            getListGamerOfClan();
            deleteGamer();
        }
        public void getListGamer(){
            String url = new StringBuilder(baseUrl)
                    .append("/gamer/list?page=").append(1).toString();
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.get(url);
            DebugLogger.info("{}",response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject data = response.getAsJsonObject("data");
            Assertions.assertTrue(data.getAsJsonArray("gamer").size() > 0);
        }
        public void getGamer(JsonObject data){
            long userId = data.get("user_id").getAsLong();
            data.addProperty("phone",username);
            data.addProperty("nick_name",nickname);
            String url = new StringBuilder(baseUrl)
                    .append("/gamer/get?id=").append(userId).toString();
            DebugLogger.info("{}", url);
            JsonObject response = OkHttpUtil.get(url);
            DebugLogger.info("{}",response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject gamer = response.getAsJsonObject("data");
            Assertions.assertEquals(data.get("clan_id").getAsLong(), gamer.get("clan_id").getAsLong());
        }
        public void createGamerError(){
            JsonObject data = new JsonObject();
            data.addProperty("phone",username);
            data.addProperty("nick_name",nickname);
            String createUrl = new StringBuilder(baseUrl)
                    .append("/gamer/create").toString();
            JsonObject response = OkHttpUtil.postJson(createUrl, data.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}",response);
            Assertions.assertTrue(response.get("error").getAsInt() == 13);
        }
        public JsonObject createGamerSuccess(){
            //create body
            JsonObject data = new JsonObject();
            data.addProperty("phone",username);
            data.addProperty("nick_name",nickname);
            data.addProperty("full_name","KhanhAd");
            data.addProperty("avatar","http://");
            JsonObject detail = new JsonObject();
            detail.addProperty("sport"," Age of Empires");
            detail.addProperty("address","NQ-NB");
            detail.addProperty("date_of_birth","07/09/2002");
            detail.add("image",new JsonArray());
            detail.addProperty("nationality","China");
            detail.addProperty("facebook_link",
                    "https://docs.google.com/document/d/1td7LtgMVFoL5xrtJ6m0zErOIYDJgBUX2Dlk-vsMpFHU/edit#heading=h.mcb4gmk6o7pq");
            detail.addProperty("telegram_link",
                    "https://docs.google.com/document/d/1td7LtgMVFoL5xrtJ6m0zErOIYDJgBUX2Dlk-vsMpFHU/edit#heading=h.mcb4gmk6o7pq");
            detail.addProperty("tiktok_link",
                    "https://docs.google.com/document/d/1td7LtgMVFoL5xrtJ6m0zErOIYDJgBUX2Dlk-vsMpFHU/edit#heading=h.mcb4gmk6o7pq");
            detail.addProperty("youtube_link",
                    "https://docs.google.com/document/d/1td7LtgMVFoL5xrtJ6m0zErOIYDJgBUX2Dlk-vsMpFHU/edit#heading=h.mcb4gmk6o7pq");
            data.add("detail",detail);
            data.add("rank_info",new JsonObject());
            data.addProperty("clan_id",clanId);
            data.addProperty("rank",1);
            String createUrl = new StringBuilder(baseUrl)
                    .append("/gamer/create").toString();
            JsonObject response = OkHttpUtil.postJson(createUrl, data.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}",response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            return response.getAsJsonObject("data");
        }
        public void deleteGamer(){
            String deleteGamer = new StringBuilder(baseUrl).append("/gamer/delete").toString();
            JsonObject payload = new JsonObject();
            payload.addProperty("username", username);
            JsonObject response =
                    OkHttpUtil.postJson(deleteGamer, payload.toString(), Common.createHeaderSystemAdmin());
            DebugLogger.info("{}", response);
        }
        public void updateGamer(JsonObject data){
            String updateGamer = new StringBuilder(baseUrl)
                    .append("/gamer/update").toString();
            data.addProperty("nick_name", nickname);
            data.addProperty("full_name", "1");
            data.addProperty("avatar", "1");
            data.add("detail", new JsonObject());
            data.addProperty("clan_id", clanIdUpdate);
            data.addProperty("rank", "1");
            data.add("rank_info",new JsonObject());
            data.addProperty("state", "0");
            String url = new StringBuilder(baseUrl).append("/gamer/update").toString();
            JsonObject response = OkHttpUtil.postJson(url, data.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }
        public void getListGamerOfClan() {
            String listGamerUrl = new StringBuilder(baseUrl)
                    .append("/gamer/list-of-clan?clan_id=").append(clanIdUpdate).toString();
            JsonObject response = OkHttpUtil.get(listGamerUrl);
            DebugLogger.info("{}", response);
            JsonArray gamers = response.getAsJsonObject("data").getAsJsonArray("gamers");
            Assertions.assertTrue(gamers.size() > 0);
        }
    }



    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
