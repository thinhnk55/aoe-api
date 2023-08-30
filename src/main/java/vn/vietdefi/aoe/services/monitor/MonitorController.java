package vn.vietdefi.aoe.services.monitor;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;
import vn.vietdefi.common.BaseResponse;
import vn.vietdefi.util.log.DebugLogger;
import vn.vietdefi.util.thread.ThreadPoolWorker;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MonitorController {
    private static MonitorController ins = null;
    public ScheduledFuture<?> loopTask;
    public SystemInfo systemInfo;
    JsonArray cacheSystemInfo;
    public long idIndex = 0;

    public static MonitorController instance() {
        if (ins == null) {
            ins = new MonitorController();
        }
        return ins;
    }

    private MonitorController() {
        systemInfo = new SystemInfo();
        cacheSystemInfo = new JsonArray();
    }

    public SystemInfo getSystemInfo() {
        return systemInfo;
    }

    private void updateCacheSystemInfo() {
        try {
            systemInfo.update();
            addSystemInfoToCache();
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.getStackTrace(e));
        }
    }

    private void addSystemInfoToCache() {
        JsonObject system = systemInfo.toJsonObject();
        system.addProperty("idIndex", ++idIndex);
        cacheSystemInfo.add(system);
        if(cacheSystemInfo.size() > 100){
            cacheSystemInfo.remove(0);
        }
    }

    public void startLoop() {
        this.loopTask = ThreadPoolWorker.instance().scheduler.scheduleAtFixedRate(
                this::updateCacheSystemInfo, 0, 5, TimeUnit.SECONDS
        );
    }

    public JsonObject getInfo() {
        return BaseResponse.createFullMessageResponse(0, "success", cacheSystemInfo);
    }
}
