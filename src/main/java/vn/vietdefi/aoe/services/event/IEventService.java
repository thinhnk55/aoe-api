package vn.vietdefi.aoe.services.event;

import com.google.gson.JsonObject;

public interface IEventService {

    JsonObject createEvent(JsonObject json);
    JsonObject lockEvent(JsonObject data);

    JsonObject getEvent(long eventId);
    JsonObject addParticipant(JsonObject json);

    JsonObject getListParticipants(long eventId, long page, long recordPerPage);
    JsonObject getEventByStatus(int state, long page, long recordPerPage);

    JsonObject getEventByMatch(long match_id);

    JsonObject getListWinning(long eventId, int luckyNumber,int limit);

    JsonObject getListEventParticipant(long userid, long page, long recordPerPage);
}
