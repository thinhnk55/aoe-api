package vn.vietdefi.aoe.api.match;

import com.google.gson.JsonObject;
import vn.vietdefi.aoe.services.match.MatchConstants;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.network.OkHttpUtil;

public class MatchUtil {
    public static JsonObject getVotingMatch(String baseUrl){
        int state = MatchConstants.STATE_VOTING;
        int page = 1;
        String getListMatchURL = new StringBuilder(baseUrl).append("/match/getlist/state")
                .append("?state=").append(state).append("&page=").append(page).toString();
        JsonObject response = OkHttpUtil.get(getListMatchURL);
        DebugLogger.info("{}", response);
        return response;
    }
    public static JsonObject getStopVotingMatch(String baseUrl){
        int state = MatchConstants.STATE_STOP_VOTING;
        int page = 1;
        String getListMatchURL = new StringBuilder(baseUrl).append("/match/getlist/state")
                .append("?state=").append(state).append("&page=").append(page).toString();
        JsonObject response = OkHttpUtil.get(getListMatchURL);
        DebugLogger.info("{}", response);
        return response;
    }
}
