package vn.vietdefi.aoe.api.profile;

import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.api.Common;
import vn.vietdefi.aoe.services.profile.ProfileConstant;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class ProfileTest {
    @BeforeAll
    static void init(){
        try {
            DOMConfigurator.configure("config/aoe/log/log4j.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nested
    class AoeAuthTest1{
        String baseUrl;
        String username;
        String password;

        @BeforeEach
        void init(){
//            baseUrl = "https://api.godoo.asia/aoe";
            baseUrl = "http://192.168.1.99:8000/aoe";
            username = "086888444";
            password = "12344321";
        }
        @RepeatedTest(1)
        void repeatTest1(){

        }
        @Test
        public void test0(){

        }
        @Test
        public void test1(){
            JsonObject response = Common.deleleUser(baseUrl, username, password);
            DebugLogger.info("{}", response);
            response = Common.registerUserSuccess(baseUrl, username, password);
            DebugLogger.info("{}", response);
            JsonObject user = response.getAsJsonObject("data");

            response = Common.getProfileSuccess(baseUrl, user);
            JsonObject profile = response.getAsJsonObject("data");
            JsonObject newProfile = GsonUtil.toJsonObject(profile.toString());

            newProfile.addProperty("lang", ProfileConstant.LANG_CHINA);
            String updateUrl = new StringBuilder(baseUrl)
                    .append("/profile/update").toString();
            OkHttpUtil.postJson(updateUrl, newProfile.toString(),Common.createHeader(user));

            response = Common.getProfileSuccess(baseUrl, user);
            profile = response.getAsJsonObject("data");
            Assertions.assertEquals(profile.toString(), newProfile.toString());

            newProfile = new JsonObject();
            newProfile.addProperty("lang", ProfileConstant.LANG_VIETNAM);
            updateUrl = new StringBuilder(baseUrl)
                    .append("/profile/update/lang").toString();
            OkHttpUtil.postJson(updateUrl, newProfile.toString(),Common.createHeader(user));

            response = Common.getProfileSuccess(baseUrl, user);
            profile = response.getAsJsonObject("data");
            Assertions.assertEquals(profile.get("lang").getAsInt(), newProfile.get("lang").getAsInt());

            Assertions.assertNotNull(profile);
            Common.deleleUser(baseUrl, username, password);
        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
