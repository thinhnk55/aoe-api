package vn.vietdefi.api.services;

import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.aoe.services.bank.AoeBankHandlerService;
import vn.vietdefi.api.services.auth.UserConstant;
import vn.vietdefi.api.vertx.ApiConfig;
import vn.vietdefi.bank.logic.BankController;
import vn.vietdefi.bank.services.IBankHandlerService;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.string.StringUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BankTest {
    @BeforeAll
    static void init(){
        try {
            DOMConfigurator.configure("config/aoe/log/log4j.xml");
            HikariClients.instance().init("config/aoe/sql/databases.json"
                    ,"config/aoe/sql/hikari.properties");
            ApiConfig.instance().init("config/aoe/http/http.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nested
    class BankTest1{
        @BeforeEach
        void init(){

        }
        @RepeatedTest(1)
        void repeatTest1(){
        }
        @Test
        public void test0(){
            JsonObject response;
            response = ApiServices.authService.changeRole("0836993400",
                    UserConstant.ROLE_SUPER_ADMIN);
            DebugLogger.info("{}", response.toString());
            response = ApiServices.authService.changeStatus("0836993400",
                    UserConstant.STATUS_ACCOUNT_GENERATE);
            DebugLogger.info("{}", response.toString());
//            response = ApiServices.authService.get("0836993400");
//            DebugLogger.info("{}", response);
//            response = ApiServices.authService.register("0836993400",
//                    "123456", UserConstant.ROLE_SUPER_ADMIN, UserConstant.STATUS_NORMAL);
//            DebugLogger.info("{}", response);
        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
