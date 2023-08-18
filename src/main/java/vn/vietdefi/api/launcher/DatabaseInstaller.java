package vn.vietdefi.api.launcher;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.log4j.xml.DOMConfigurator;
import vn.vietdefi.util.sql.HikariClients;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class DatabaseInstaller {
    public static void main(String[] args) {
        try {
            DOMConfigurator.configure("config/aoe/log/log4j.xml");
            HikariClients.instance().init("config/aoe/sql/databases.json",
                    "config/aoe/sql/hikari.properties");
            databaseInstall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void databaseInstall() throws Exception {
        ScriptRunner scriptRunner = new ScriptRunner(
                HikariClients.instance().defaultHikariClient().getConnection());
        File file = new File("sql/aoe_donate.sql");
        Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        scriptRunner.runScript(reader);
    }
}
