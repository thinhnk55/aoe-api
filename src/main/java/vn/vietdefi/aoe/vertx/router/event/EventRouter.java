package vn.vietdefi.aoe.vertx.router.event;

import com.google.gson.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.aoe.services.AoeServices;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.json.GsonUtil;
import vn.vietdefi.util.log.DebugLogger;

public class EventRouter {
    public static void createEvent(RoutingContext rc){
        try{
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            JsonObject response = AoeServices.eventService.createEvent(json);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }
    public static void lockEvent(RoutingContext rc){
        try{
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            JsonObject response = AoeServices.eventService.lockEvent(json);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }

    public static void getEvent(RoutingContext rc){
        try{
            long eventId =  Long.parseLong(rc.request().getParam("eventId"));
            JsonObject response = AoeServices.eventService.getEvent(eventId);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }

    public static void addParticipant(RoutingContext rc){
        try{
            long userId = Long.parseLong(rc.request().getHeader("userid"));
            String data = rc.body().asString();
            JsonObject json = GsonUtil.toJsonObject(data);
            json.addProperty("userid",userId);
            JsonObject response = AoeServices.eventService.addParticipant(json);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }
    public static void getListParticipants(RoutingContext rc){
        try{
            long eventId =  Long.parseLong(rc.request().getParam("eventId"));
            long page = Long.parseLong(rc.request().getParam("page","1"));
            JsonObject response = AoeServices.eventService.getListParticipants(eventId,page,20);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }
    public static void getEventByState(RoutingContext rc){
        try{
            int state =  Integer.parseInt(rc.request().getParam("state"));
            long page = Long.parseLong(rc.request().getParam("page","1"));
            JsonObject response = AoeServices.eventService.getEventByStatus(state,page,20);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }

    public static void getListWinning(RoutingContext rc){
        try{
            int luckyNumber =  Integer.parseInt(rc.request().getParam("lucky_number"));
            long evenId = Long.parseLong(rc.request().getParam("eventId"));
            int limit = Integer.parseInt(rc.request().getParam("top"));
            JsonObject response = AoeServices.eventService.getListWinning(evenId,luckyNumber,limit);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }

    public static void getEventByMatch(RoutingContext rc){
        try{
            long matchId =  Long.parseLong(rc.request().getParam("match_id"));
            JsonObject response = AoeServices.eventService.getEventByMatch(matchId);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }

    public static void getListHistoryParticipant(RoutingContext rc) {
        try{
            long userid =  Long.parseLong(rc.request().getHeader("userid"));
            int page =  Integer.parseInt(rc.request().getParam("page","1"));
            JsonObject response = AoeServices.eventService
                    .getListEventParticipant(userid,page, 15);
            rc.response().end(response.toString());
        }
        catch (Exception e){
            String stacktrace = ExceptionUtils.getStackTrace(e);
            DebugLogger.error(stacktrace);
            JsonObject response = BaseResponse.createFullMessageResponse(1,"system_error");
            rc.response().end(response.toString());
        }
    }
}
