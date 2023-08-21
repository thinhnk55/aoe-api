package vn.vietdefi.api.services;

import com.google.gson.JsonObject;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.api.services.auth.AuthService;
import vn.vietdefi.api.services.auth.IAuthService;
import vn.vietdefi.api.services.auth.UserConstant;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;
import vn.vietdefi.util.sql.HikariClients;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthTest {
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
    class AuthTest1{
        String baseUrl;
        @BeforeEach
        void init(){
            baseUrl = "https://api.godoo.asia/aoe";
        }
        @RepeatedTest(1)
        void repeatTest1(){
        }
        @Test
        public void test0(){
            IAuthService service = new AuthService();
            String username = "0836993400";
            String password = "123456";
            JsonObject response = service.login(username, password);
            DebugLogger.info("{}", response);
            response = service.changeStatus(6, UserConstant.STATUS_ACCOUNT_GENERATE);
            DebugLogger.info("{}", response);
            response = service.register(username, password, UserConstant.ROLE_SUPER_ADMIN,
                    UserConstant.STATUS_NORMAL);
            DebugLogger.info("{}", response);
            service.changeStatus(6, UserConstant.STATUS_ACCOUNT_GENERATE);
        }
        @Test
        public void test1(){
            String s = "plus://plus.vn/SpendTransList/expand?refNo=003dTF230812216736764";
            DebugLogger.info(s.substring(s.length()-17, s.length()-1));
            s = "Mô tả: MBVCB.4049553539.071571.0915434549 Donate KD15.CT tu 1029707411 NGUYEN DUY KHANH toi 0915434549 NGUYEN DUY KHANH tai Timo by Ban Viet Bank";
            int index = s.indexOf(":");
            DebugLogger.info(s.substring(index+2));
        }
        @Test
        public void test2(){
            String s = "Số dư tài khoản vừa giảm 100.0 VND vào 12/08/2023 08:49 VN ";
            s = "Số dư tài khoản vừa tăng 1.000 vào 17/08/2023 16:40 VN";
            String regex = "\\d+([.]\\d+)*";
            Pattern accountBalancePattern = Pattern.compile(regex);
            Matcher matcher = accountBalancePattern.matcher(s);
            String data = "not_found";
            if (matcher.find()) {
                data = matcher.group(0);
            }
            DebugLogger.info(data);
        }
    }

    @BeforeAll
    static void done(){
        DebugLogger.info("AuthTest done");
    }
}
