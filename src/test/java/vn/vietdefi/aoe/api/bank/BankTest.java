package vn.vietdefi.aoe.api.bank;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.jupiter.api.*;
import vn.vietdefi.util.log.DebugLogger;

public class BankTest {
    @BeforeAll
    static void init() {
        try {
            DOMConfigurator.configure("config/aoe/log/log4j.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nested
    class BankTest1 {
        String baseUrl;
        String username;
        String password;


        @BeforeEach
        void init() {
            baseUrl = "http://127.0.0.1:8000/aoe";
            username = "0915434549";
            password = "KhanhAk54";
        }
        @Test
        public void test0() {
            //login bank
        }
    }


    @BeforeAll
    static void done() {
        DebugLogger.info("AuthTest done");
    }
}
