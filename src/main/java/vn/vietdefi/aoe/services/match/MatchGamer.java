package vn.vietdefi.aoe.services.match;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class MatchGamer {

    public static JsonObject createTeamPlayer(JsonArray data) {
        String stInsertMatchGamer = new StringBuilder("INSERT INTO aoe_match_gamer (match_id,gamer_id,team,gamer_nick_name,avatar)")
                .append(" VALUES(?,?,?,?)").toString();
        try (Connection connection = HikariClients.instance().defaulSQLJavaBridge().sqlClient.getConnection();
             PreparedStatement st1 = connection.prepareStatement(stInsertMatchGamer)
        ) {
            connection.setAutoCommit(false);
            for (JsonElement element : data) {
                JsonObject gamer = element.getAsJsonObject();
                st1.setLong(1, gamer.get("match_id").getAsLong());
                st1.setLong(2, gamer.get("gamer_id").getAsLong());
                st1.setInt(3, gamer.get("team").getAsInt());
                st1.setString(4, gamer.get("gamer_nick_name").getAsString());
                st1.setString(5, gamer.get("avatar").getAsString());
                st1.addBatch();
            }
            int[] result = st1.executeBatch();
            for (int i : result) {
                if (i != 1) {
                    DebugLogger.error("insert into match gamer {}", data);
                    connection.rollback();
                }
            }
            connection.commit();
            return BaseResponse.createFullMessageResponse(1, "system_error");
        } catch (Exception e) {
            DebugLogger.error("insert into match gamer {}", data);
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }

    }
}
