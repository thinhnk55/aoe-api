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
    public JsonObject createEvent(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long matchId = data.get("match_id").getAsLong();
            JsonObject response = AoeServices.matchService.getById(matchId);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return response;
            }
            data.addProperty("start_time", System.currentTimeMillis());
            bridge.insertObjectToDB("aoe_event", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    public JsonObject updateStateByEventId(JsonObject data) {
        try {
            long eventId = data.get("event_id").getAsLong();
            int state = data.get("state").getAsInt();
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            JsonObject response = checkStateByEventId(state, eventId);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return response;
            }
            if (state != EventConstants.EVENT_FINISHED) {
                String query = "UPDATE aoe_event SET state = ? WHERE id = ?";
                int row = bridge.update(query, state, eventId);
                if (row == 0) {
                    return BaseResponse.createFullMessageResponse(12, "update_failure");
                }
            } else {
                int winningNumber = data.get("winning_number").getAsInt();
                String query = "UPDATE aoe_event SET state = ?, winning_number = ? WHERE id = ? AND state = ?";
                int row = bridge.update(query, state, winningNumber, eventId, EventConstants.EVENT_DRAWING);
                if (row == 0) {
                    return BaseResponse.createFullMessageResponse(12, "update_failure");
                }
            }
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system");
        }
    }

    private JsonObject checkStateByEventId(int state, long eventId) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query = "SELECT state FROM aoe_event WHERE id = ? ";
            JsonObject event = bridge.queryOne(query, eventId);
            if (event == null) {
                return BaseResponse.createFullMessageResponse(10, "event_not_found");
            } else {
                int currentState = event.get("state").getAsInt();
                if (currentState == state || currentState == EventConstants.EVENT_FINISHED) {
                    return BaseResponse.createFullMessageResponse(11, "invalid_operation");
                }
            }
            return BaseResponse.createFullMessageResponse(0, "success");
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
                return BaseResponse.createFullMessageResponse(10, "event_not_found");
            }
            json.add("participants", getNumberOfParticipant(eventId).get("total"));
            return BaseResponse.createFullMessageResponse(0, "success", json);
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
            JsonObject data = bridge.queryOne(query, eventId);
            return data;
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject joinEvent(long userId, JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            if (userId != data.get("user_id").getAsLong()) {
                return BaseResponse.createFullMessageResponse(12, "join_reject");
            }
            long eventId = data.get("event_id").getAsLong();
            String query = "SELECT * FROM aoe_event WHERE id = ? AND state = ?";
            JsonObject event = bridge.queryOne(query, eventId, EventConstants.EVENT_ON_GOING);
            if(event == null){
                return BaseResponse.createFullMessageResponse(10, "event_finished_or_not_exist");
            }
            query = "SELECT user_id FROM aoe_event_participants WHERE event_id = ? AND user_id = ? ";
            if (bridge.queryExist(query, eventId, userId)) {
                return BaseResponse.createFullMessageResponse(11, "participated");
            }
            int maxNumber = event.get("max_number").getAsInt();
            int luckyNumber = data.get("lucky_number").getAsInt();
            if(luckyNumber < 0 || luckyNumber > maxNumber){
                return BaseResponse.createFullMessageResponse(13, "invalid_number");
            }
            data.addProperty("create_time", System.currentTimeMillis());
            bridge.insertObjectToDB("aoe_event_participants", data);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject cancelParticipant(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long userId = data.get("user_id").getAsLong();
            long eventId = data.get("event_id").getAsLong();
            String query = "UPDATE aoe_event_participants SET state = ? WHERE event_id = ? AND user_id = ? AND state = ?";
            int row = bridge.update(query, EventConstants.CANCELLED_PARTICIPANT, eventId, userId, EventConstants.QUEUED_PARTICIPANT);
            if(row == 0){
                return BaseResponse.createFullMessageResponse(10, "cancel_failure");
            }
            return BaseResponse.createFullMessageResponse(0, "success");
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject awardParticipant(JsonObject data) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long userId = data.get("user_id").getAsLong();
            long eventId = data.get("event_id").getAsLong();
            long amount = data.get("amount").getAsLong();
            String query = "UPDATE aoe_event_participants SET state = ? WHERE event_id = ? AND user_id = ? AND state = ?";
            int row = bridge.update(query, EventConstants.REWARDED_PARTICIPANT, eventId, userId, EventConstants.QUEUED_PARTICIPANT);
            if(row == 0){
                return BaseResponse.createFullMessageResponse(10, "award_failure");
            }
            JsonObject detail = new JsonObject();
            detail.addProperty("amount", amount);
            detail.addProperty("user_id", userId);
            query = "UPDATE aoe_event SET detail = ? WHERE id = ?";
            row = bridge.update(query, detail, eventId);
            if(row == 0){
                return BaseResponse.createFullMessageResponse(10, "award_failure");
            }
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
            JsonObject response = getEvent(eventId);
            if (!BaseResponse.isSuccessFullMessage(response)) {
                return response;
            }
            String query = "SELECT COUNT(*) AS total FROM aoe_event_participants WHERE event_id = ? ";
            JsonObject data = bridge.queryOne(query, eventId);
            query = "SELECT * FROM aoe_event_participants WHERE event_id = ? LIMIT ? OFFSET ?";
            JsonArray json = bridge.query(query, eventId, recordPerPage, offset);
            data.add("participant", json);
            return BaseResponse.createFullMessageResponse(0, "success", data);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

    @Override
    public JsonObject getListEventByState(int state, long page, long recordPerPage) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            long offset = (page - 1) * recordPerPage;
            String query = "SELECT * FROM aoe_event WHERE state = ? LIMIT ? OFFSET ?";
            JsonArray json = bridge.query(query, state, recordPerPage, offset);
            for (JsonElement element : json) {
                JsonObject event = element.getAsJsonObject();
                long eventId = event.get("id").getAsLong();
                event.add("participants", getNumberOfParticipant(eventId).get("total"));
            }
            JsonObject result = new JsonObject();
            result.add("event", json);
            return BaseResponse.createFullMessageResponse(0, "success", result);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }

//    @Override
//    public JsonObject getEventByMatch(long match_id) {
//        try {
//            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
//            JsonObject response = AoeServices.matchService.getById(match_id);
//            if (!BaseResponse.isSuccessFullMessage(response)) {
//                return response;
//            }
//            JsonObject match = response.getAsJsonObject("data");
//            String link = match.getAsJsonObject("detail").get("link_livestream").getAsString();
//            String query = "SELECT * FROM aoe_event WHERE match_id = ? AND state = ?";
//            JsonObject data = bridge.queryOne(query, match_id, EventConstants.EVENT_ON_GOING);
//            if (data == null) {
//                return BaseResponse.createFullMessageResponse(11, "event_unavailable");
//            }
//            data.addProperty("link_livestream", link);
//            data.add("participants", getNumberOfParticipant(data.get("id").getAsLong()).get("total"));
//            return BaseResponse.createFullMessageResponse(0, "success", data);
//        } catch (Exception e) {
//            String stacktrace = ExceptionUtils.getStackTrace(e);
//            DebugLogger.error(stacktrace);
//            return BaseResponse.createFullMessageResponse(1, "system_error");
//        }
//    }

    @Override
    public JsonObject getListWinning(long eventId, int luckyNumber, int limit) {
        try {
            SQLJavaBridge bridge = HikariClients.instance().defaulSQLJavaBridge();
            String query =
                    new StringBuilder("SELECT user_id, lucky_number, create_time,ABS(? - lucky_number) AS margin_of_error\n")
                            .append("FROM aoe_event_participants\n")
                            .append("WHERE event_id = ? AND state = ?\n")
                            .append("ORDER BY ABS(? - lucky_number), create_time\n")
                            .append("LIMIT ?").toString();
            JsonArray data = bridge.query(query, luckyNumber, eventId, EventConstants.QUEUED_PARTICIPANT, luckyNumber, limit);
            for (JsonElement element : data) {
                long userId = element.getAsJsonObject().get("user_id").getAsLong();
                JsonObject profile = AoeServices.profileService.getUserProfileByUserId(userId);
                element.getAsJsonObject().add("profile", profile.getAsJsonObject("data"));
            }
            JsonObject result = new JsonObject();
            result.add("listWinning", data);
            return BaseResponse.createFullMessageResponse(0, "success", result);
        } catch (Exception e) {
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            return BaseResponse.createFullMessageResponse(1, "system_error");
        }
    }
}
