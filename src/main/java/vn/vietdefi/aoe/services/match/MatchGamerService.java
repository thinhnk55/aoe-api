package vn.vietdefi.aoe.services.match;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class MatchGamerService {

    public static JsonObject createTeamPlayer(long match_id, JsonArray data) {
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

    public static JsonObject updateTeamPlayer(long match_id, JsonArray data) {
        String stUpdateMatch = "DELETE FROM aoe_match_gamer WHERE match_id = ?";

        try (Connection connection = HikariClients.instance().defaulSQLJavaBridge().sqlClient.getConnection();
             PreparedStatement st = connection.prepareStatement(stUpdateMatch)) {
            connection.setAutoCommit(false);
            st.setLong(1, match_id);
            if (st.executeUpdate() == 0) {
                connection.rollback();
                return BaseResponse.createFullMessageResponse(11, "update_error");
            }
            connection.commit();
            return createTeamPlayer(match_id, data);
        } catch (Exception e) {
            DebugLogger.error("insert into match gamer {}", data);
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public static JsonObject updateStateTeamPlayer(long matchId, int state) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "UPDATE aoe_match_gamer SET state = ? WHERE match_id = ?";
            bridge.update(query, state, matchId);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public static JsonObject updateResultMatch(long matchId, int format, JsonArray data) {
        String updateStatement = "UPDATE aoe_match_gamer SET result = ? , state = ? WHERE match_id = ? AND team = ?";

        try (Connection connection = HikariClients.instance().defaulSQLJavaBridge().sqlClient.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateStatement)
        ) {
            connection.setAutoCommit(false);

            for (JsonElement element : data) {
                JsonObject gamer = element.getAsJsonObject();
                int result = gamer.get("result").getAsInt();
                int team = gamer.get("team").getAsInt();
                int state = calculateState(result, format, team, data);

                preparedStatement.setInt(1, result);
                preparedStatement.setInt(2, state);
                preparedStatement.setLong(3, matchId);
                preparedStatement.setInt(4, team);
                preparedStatement.addBatch();
            }

            int[] batchResult = preparedStatement.executeBatch();
            for (int i : batchResult) {
                if (i == Statement.EXECUTE_FAILED) {
                    connection.rollback();
                    return BaseResponse.createFullMessageResponse(1, "system_error");
                }
            }

            connection.commit();
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            DebugLogger.error("Insert into match gamer failed: {}", data);
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    private static int calculateState(int result, int format, int team, JsonArray data) {
        if (format == MatchConstant.FORMAT_FREE) {
            return (result > 3) ? MatchConstant.STATE_GAMER_LOSER : MatchConstant.STATE_GAMER_WINNER;
        } else {
            int teamResultValue1 = data.get(0).getAsJsonObject().get("result").getAsInt();
            int teamResultValue2 = data.get(1).getAsJsonObject().get("result").getAsInt();
            if (team == data.get(0).getAsJsonObject().get("result").getAsInt()) {
                return (teamResultValue1 > teamResultValue2) ? MatchConstant.STATE_GAMER_WINNER : MatchConstant.STATE_GAMER_LOSER;
            } else {
                return (teamResultValue1 < teamResultValue2) ? MatchConstant.STATE_GAMER_WINNER : MatchConstant.STATE_GAMER_LOSER;
            }
        }
    }

    public static JsonObject getListMatchByGamerId(long gamer_id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT match_id FROM aoe_match_gamer WHERE user_id = ? AND state = ? ";
            JsonArray data = bridge.query(query, gamer_id, MatchConstant.STATE_GAMER_MATCH_PENDING);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

}
