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
import java.sql.Statement;

public class MatchGamer {

    public static JsonObject createTeamPlayer(JsonArray data, long match_id) {
        String stInsertMatchGamer = "INSERT INTO aoe_match_gamer (match_id, user_id, team, nick_name, avatar) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = HikariClients.instance().defaulSQLJavaBridge().sqlClient.getConnection();
             PreparedStatement st1 = connection.prepareStatement(stInsertMatchGamer)
        ) {
            connection.setAutoCommit(false);
            for (JsonElement element : data) {
                JsonObject gamer = element.getAsJsonObject();
                st1.setLong(1, match_id);
                st1.setLong(2, gamer.get("user_id").getAsLong());
                st1.setInt(3, gamer.get("team").getAsInt());
                st1.setString(4, gamer.get("nick_name").getAsString());
                st1.setString(5, gamer.get("avatar").getAsString());
                st1.addBatch();
            }
            int[] result = st1.executeBatch();
            for (int i : result) {
                if (i == Statement.EXECUTE_FAILED) {
                    connection.rollback();
                    return BaseResponse.createFullMessageResponse(1, "system_error");
                }
            }
            connection.commit();
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            DebugLogger.error("insert into match gamer {}", data);
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public static JsonObject updateTeamPlayer(JsonArray data, long match_id) {
        String stInsertMatchGamer = "INSERT INTO aoe_match_gamer (match_id, user_id, team, nick_name, avatar) VALUES (?, ?, ?, ?, ?)";
        String stUpdateMatch = "DELETE FROM aoe_match_gamer WHERE match_id = ?";

        try (Connection connection = HikariClients.instance().defaulSQLJavaBridge().sqlClient.getConnection();
             PreparedStatement st = connection.prepareStatement(stUpdateMatch);
             PreparedStatement st1 = connection.prepareStatement(stInsertMatchGamer)
        ) {
            connection.setAutoCommit(false);
            if(st.executeUpdate() == 0){
                connection.rollback();
                return BaseResponse.createFullMessageResponse(11, "update_error");
            }
            for (JsonElement element : data) {
                JsonObject gamer = element.getAsJsonObject();
                st1.setLong(1, match_id);
                st1.setLong(2, gamer.get("user_id").getAsLong());
                st1.setInt(3, gamer.get("team").getAsInt());
                st1.setString(4, gamer.get("nick_name").getAsString());
                st1.setString(5, gamer.get("avatar").getAsString());
                st1.addBatch();
            }
            int[] result = st1.executeBatch();
            for (int i : result) {
                if (i == Statement.EXECUTE_FAILED) {
                    connection.rollback();
                    return BaseResponse.createFullMessageResponse(11, "update_error");
                }
            }
            connection.commit();
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            DebugLogger.error("insert into match gamer {}", data);
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

}
