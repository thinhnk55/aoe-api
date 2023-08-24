package vn.vietdefi.aoe.api.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.aoe.services.event.EventConstants;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;
import vn.vietdefi.util.string.StringUtil;

public class EventTest {
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
        long matchId;
        long userId;
        String token;
        @BeforeEach
        void init() {
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://127.0.0.1:8000/aoe";
            matchId = 1;
            userId = 71;
            token = "044uldwgrtgrjaecknqvxh79khpzkspb";
        }

        @RepeatedTest(1)
        void repeatTest1() {

        }

        @Test
        public void test0() {
            JsonObject event = createEventSuccess();
            createEventFailure();
            long eventId = event.get("id").getAsLong();
            getEvent(eventId);
            joinEvent(eventId);
            listParticipant(eventId);
            listByState();
            updateState(eventId);
        }
        public JsonObject createEventSuccess() {
            JsonObject data = new JsonObject();
            data.addProperty("match_id", matchId);
            data.addProperty("reward_date", 1687516800000L);
            data.addProperty("award", 100000000);
            data.addProperty("max_number", 9999);
            String createUrl = new StringBuilder(baseUrl)
                    .append("/event/create").toString();
            JsonObject response = OkHttpUtil.postJson(createUrl, data.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            return response.getAsJsonObject("data");
        }
        public void createEventFailure() {
            JsonObject data = new JsonObject();
            data.addProperty("match_id", matchId);
            String createUrl = new StringBuilder(baseUrl)
                    .append("/event/create").toString();
            JsonObject response = OkHttpUtil.postJson(createUrl, data.toString(),Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(response.get("error").getAsInt() == 1);
        }
        public void getEvent(long id) {
            String getUrl = new StringBuilder(baseUrl)
                    .append("/event/get?event_id=").append(id).toString();
            JsonObject response = OkHttpUtil.get(getUrl);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonObject event = response.getAsJsonObject("data");
            DebugLogger.info("{}", response);
            Assertions.assertTrue(event.get("match_id").getAsLong() == matchId);
        }
        public void joinEvent(long eventId){
            JsonObject data = new JsonObject();
            data.addProperty("user_id", userId);
            data.addProperty("event_id", eventId);
            data.addProperty("phone", "012367912");
            data.addProperty("lucky_number", "1234");
            String joinUrl = new StringBuilder(baseUrl)
                    .append("/event/join").toString();
            JsonObject response = OkHttpUtil.postJson(joinUrl, data.toString(),Common.createHeader(userId, token));
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
        }
        public void listParticipant(long eventId){
            String createUrl = new StringBuilder(baseUrl)
                    .append("/event/list-participant?event_id=").append(eventId).append("&page=1").toString();
            JsonObject response = OkHttpUtil.get(createUrl);
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            long total = response.getAsJsonObject("data").get("total").getAsInt();
            Assertions.assertTrue(total > 0);
        }
        public void listByState(){
            String createUrl = new StringBuilder(baseUrl)
                    .append("/event/list-by-state?state=").append(EventConstants.EVENT_ON_GOING)
                    .append("&page=1").toString();
            JsonObject response = OkHttpUtil.get(createUrl);
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            JsonArray event = response.getAsJsonObject("data").getAsJsonArray("event");
            Assertions.assertTrue(event.size() > 0);
        }
        public void updateState(long eventId){
            JsonObject data = new JsonObject();
            data.addProperty("event_id", eventId);
            //Lock
            data.addProperty("state", EventConstants.EVENT_LOCKED);
            String updateUrl = new StringBuilder(baseUrl)
                    .append("/event/update-state")
                    .toString();
            JsonObject response = OkHttpUtil.postJson(updateUrl, data.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            //On going
            data.addProperty("state", EventConstants.EVENT_ON_GOING);
            response = OkHttpUtil.postJson(updateUrl, data.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            //Drawing
            data.addProperty("state", EventConstants.EVENT_DRAWING);
            response = OkHttpUtil.postJson(updateUrl, data.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
            //Finish
            data.addProperty("state", EventConstants.EVENT_FINISHED);
            data.addProperty("winning_number", 1234);
            response = OkHttpUtil.postJson(updateUrl, data.toString(), Common.createHeaderAdmin());
            DebugLogger.info("{}", response);
            Assertions.assertTrue(BaseResponse.isSuccessFullMessage(response));
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