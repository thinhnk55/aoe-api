package vn.vietdefi.aoe.services.event;

import com.google.gson.JsonObject;

public interface IEventService {

    JsonObject createEvent(JsonObject json);
    JsonObject getEvent(long eventId);
    JsonObject getListParticipants(long eventId, long page, long recordPerPage);
    JsonObject getListEventByState(int state, long page, long recordPerPage);
//    JsonObject getEventByMatch(long match_id);
    JsonObject getListWinning(long eventId, int luckyNumber, int limit);
    JsonObject updateStateByEventId(JsonObject json);
    JsonObject joinEvent(long userId, JsonObject json);

    JsonObject cancelParticipant(JsonObject data);

    JsonObject awardParticipant(JsonObject data);
}
