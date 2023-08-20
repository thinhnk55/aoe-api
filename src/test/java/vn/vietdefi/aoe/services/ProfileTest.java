package vn.vietdefi.aoe.services;

import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.services.profile.ProfileConstant;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;

public class ProfileTest {
    @BeforeAll
    static void init(){
        try {
            DOMConfigurator.configure("config/aoe/log/log4j.xml");
            HikariClients.instance().init("config/aoe/sql/databases.json"
                    ,"config/aoe/sql/hikari.properties");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nested
    class DonateTest1{
        @BeforeEach
        void init(){

        }
        @RepeatedTest(1)
        void repeatTest1(){
        }
        @Test
        public void test0(){
            JsonObject response = AoeServices.profileService.getUserProfileByUserId(6);
            JsonObject data = response.getAsJsonObject("data");
            data.addProperty("nick_name", "jimmy_caster");
            response = AoeServices.profileService.updateUserProfile(5, data);
            DebugLogger.info("{}", response);
            response = AoeServices.profileService.updateUserProfile(6, data);
            DebugLogger.info("{}", response);
            response = AoeServices.profileService.getUserProfileByUserId(6);
            DebugLogger.info("{}", response);
            response = AoeServices.profileService.updateLanguage(6,
                    ProfileConstant.LANG_VIETNAM);
            response = AoeServices.profileService.updateNickName(6,
                    "aoe_boss");
            response = AoeServices.profileService.getUserProfileByUserId(6);
            DebugLogger.info("{}", response);
            response = AoeServices.profileService.searchProfile("400");
            DebugLogger.info("{}", response);
        }
        @Test
        public void test1(){

        }
        @Test
        public void test2(){

        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
