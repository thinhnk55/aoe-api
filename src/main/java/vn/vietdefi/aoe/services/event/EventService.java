package vn.vietdefi.aoe.services.event;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.sql.HikariClients;
import vn.vietdefi.util.sql.SQLJavaBridge;

public class EventService implements IEventService {
    @Override
    public JsonObject createEvent(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long matchId = json.get("match_id").getAsLong();
            JsonObject match = AoeServices.matchService.getById(matchId);
            if (!BaseResponse.isSuccessFullMessage(match)) {
                return match;
            }
            JsonObject insertToDb = new JsonObject();
            insertToDb.addProperty("match_id", matchId);
            insertToDb.addProperty("reward_date", json.get("reward_date").getAsLong());
            insertToDb.addProperty("start_time", System.currentTimeMillis());
            insertToDb.add("detail", json.get("detail"));
            bridge.insertObjectToDB("aoe_event", insertToDb);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject lockEvent(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
//            int state = data.get("state").getAsInt();
            long eventId = data.get("event_id").getAsLong();
            JsonObject update = updateStateEvent(EventState.EVENT_LOCKED, eventId);
            if (!BaseResponse.isSuccessFullMessage(update)) {
                return update;
            }
            String query = "SELECT * FROM aoe_event WHERE id =?";
            return BaseResponse.createFullMessageResponse(0, "success", bridge.queryOne(query, eventId));
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    private JsonObject updateStateEvent(int state, long eventId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject check = checkState(state, eventId);
            if (!BaseResponse.isSuccessFullMessage(check)) {
                return check;
            }
            String query = "UPDATE aoe_event SET state = ? WHERE id = ? ";
            bridge.update(query, state, eventId);
            return BaseResponse.createFullMessageResponse(0, "success");

        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system");
        }


    }

    private JsonObject checkState(int state, long eventId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT state FROM aoe_event WHERE id = ? ";
            JsonObject event = bridge.queryOne(query, eventId);
            if (event == null) {
                return BaseResponse.createFullMessageResponse(10, "not_found_event");
            }
            if (event.get("state").getAsInt() > state || state - event.get("state").getAsInt() > 1) {
                return BaseResponse.createFullMessageResponse(11, "Invalid_operation");
            }
            return BaseResponse.createFullMessageResponse(0, "success", bridge.queryOne(query, eventId));
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }

    }

    @Override
    public JsonObject getEvent(long eventId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM aoe_event WHERE id = ? ";
            JsonObject json = bridge.queryOne(query, eventId);
            if (json == null) {
                return BaseResponse.createFullMessageResponse(10, "not_found_event");
            }
            json.add("participants", getNumberOfParticipant(eventId).get("total"));
            return BaseResponse.createFullMessageResponse(0, "success", json);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject addParticipant(JsonObject json) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long userId = json.get("userid").getAsLong();
            long eventId = json.get("id").getAsLong();
            String query = "SELECT id FROM aoe_event WHERE id = ? AND state = 0";
            if (!bridge.queryExist(query, eventId)) {
                return BaseResponse.createFullMessageResponse(10, "event_finished_or_not_exist");
            }
            query = "SELECT user_id FROM aoe_event_participants WHERE event_id = ? AND user_id = ? ";
            if (bridge.queryExist(query, eventId, userId)) {
                return BaseResponse.createFullMessageResponse(11, "participated");
            }
            JsonObject insertToDb = new JsonObject();
            insertToDb.addProperty("id", eventId);
            insertToDb.addProperty("user_id", userId);
            insertToDb.addProperty("lucky_number", json.get("lucky_number").getAsInt());
            insertToDb.addProperty("create_time", System.currentTimeMillis());
            bridge.insertObjectToDB("aoe_event_participants", insertToDb);
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getListParticipants(long eventId, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            String query = "SELECT * FROM aoe_event_participants WHERE event_id = ? LIMIT ? OFFSET ?";
            JsonArray json = bridge.query(query, eventId, recordPerPage, offset);
//            for (JsonElement user : json) {
//                AoeServices.profileService.getUserProfileByUserId(user.getAsJsonObject());
//            }
            query = "SELECT COUNT(*) AS total FROM aoe_event_participants  WHERE event_id = ? ";
            JsonObject data = bridge.queryOne(query, eventId);
            data.add("participant", json);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getEventByStatus(int state, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * 20;
            String query = "SELECT id,reward_date,match_id,state,lucky_number FROM aoe_event WHERE state = ? LIMIT ? OFFSET ?";
            JsonArray json = bridge.query(query, state, 20, offset);
            long eventId = 0;
            for (JsonElement event : json) {
                eventId = event.getAsJsonObject().get("id").getAsLong();
                event.getAsJsonObject().add("participants", getNumberOfParticipant(eventId).get("total"));
            }
            query = "SELECT count(*) AS total FROM aoe_event_participants WHERE  event_id = ? ";
            JsonObject result = new JsonObject();

            result.addProperty("total_page",bridge.queryOne(query,eventId).get("total").getAsLong()/recordPerPage + 1);
            result.add("event",json);
            return BaseResponse.createFullMessageResponse(0, "success", result);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getEventByMatch(long match_id) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT * FROM event WHERE match_id = ? AND status = 0"; // status 0 is take place
            JsonObject json = bridge.queryOne(query, match_id);

            JsonObject linkStream = AoeServices.matchService.getById(match_id).get("data").getAsJsonObject().get("detail").getAsJsonObject();
            json.addProperty("link_livestream", linkStream.get("link_livestream").getAsString());
            json.addProperty("participants", getNumberOfParticipant(json.get("id").getAsLong()).get("total").getAsLong());
            return BaseResponse.createFullMessageResponse(0, "success", json);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getListWinning(long eventId, int luckyNumber, int limit) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query =
                    new StringBuilder("SELECT e.user_id, e.lucky_number, e.create_time,ABS(? - e.lucky_number) AS margin_of_error , \n")
                            .append("FROM event_participants e\n")
                            .append("WHERE e.event_id = ?\n")
                            .append("GROUP BY e.event_id, e.lucky_number\n")
                            .append("ORDER BY ABS(? - e.lucky_number), e.create_time\n")
                            .append("LIMIT ?").toString();
            JsonArray json = bridge.query(query, luckyNumber, eventId, luckyNumber, limit);

//            for (JsonElement user : json) {
//                AoeServices.profileService.getUserProfile(user.getAsJsonObject());
//                user.getAsJsonObject().addProperty("lucky_number_of_event", luckyNumber);
//            }
            return BaseResponse.createFullMessageResponse(0, "success", json);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getListEventParticipant(long userid, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            String query = "SELECT event_id,lucky_number FROM event_participants  WHERE user_id = ? LIMIT ? OFFSET ?";
            JsonArray json = bridge.query(query, userid, recordPerPage, offset);
            JsonArray data = new JsonArray();
            for (JsonElement event : json) {
                long eventId = event.getAsJsonObject().get("event_id").getAsLong();
                JsonObject detail_event = getEvent(eventId).get("data").getAsJsonObject();
                detail_event.remove("detail");
                detail_event.add("lucky_number_of_user", event.getAsJsonObject().get("lucky_number"));
                data.add(detail_event);
            }
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject getNumberOfParticipant(long eventId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT COUNT(*) AS total FROM aoe_event_participants WHERE event_id = ? ";
            return bridge.queryOne(query, eventId);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
