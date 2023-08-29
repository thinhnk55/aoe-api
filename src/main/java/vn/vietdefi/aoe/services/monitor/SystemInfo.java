package vn.vietdefi.aoe.services.monitor;

import com.google.gson.JsonObject;
import com.sun.management.OperatingSystemMXBean;
import vn.vietdefi.util.json.GsonUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

public class SystemInfo {
    public double cpuLoad;
    public long runtimeTotalMemory;
    public long runtimeFreeMemory;
    public long runtimeUsedMemory;
    public long committedHeapMemoryUse;
    public long usedHeapMemoryUse;
    public long maxHeapMemoryUse;
    public long committedHeapMemoryUseNonHeap;
    public long usedHeapMemoryUseNonHeap;
    public long maxHeapMemoryUseNonHeap;
    public long totalMemory;
    public long freeMemory;
    public long usedMemory;
    public SystemInfo(){

    }
    public JsonObject toJsonObject(){
        return GsonUtil.toJsonObject(GsonUtil.toJsonString(this));
    }
    public void update(){
        double cpuLoad = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getProcessCpuLoad() * (double) 100;
        Runtime runtime = Runtime.getRuntime();
        long runtimeTotalMemory = runtime.totalMemory() >> 20;
        long runtimeFreeMemory = runtime.freeMemory() >> 20;

        long runtimeUsedMemory = runtimeTotalMemory - runtimeFreeMemory;

        MemoryUsage memoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();

        long committedHeapMemoryUse = memoryUsage.getCommitted() >> 20;
        long usedHeapMemoryUse = memoryUsage.getUsed() >> 20;
        long maxHeapMemoryUse = memoryUsage.getMax() >> 20;

        MemoryUsage memoryUsageNonHeap = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        long committedHeapMemoryUseNonHeap = memoryUsageNonHeap.getCommitted() >> 20;
        long usedHeapMemoryUseNonHeap = memoryUsageNonHeap.getUsed() >> 20;
        long maxHeapMemoryUseNonHeap = memoryUsageNonHeap.getMax() >> 20;

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        long totalMemory = osBean.getTotalPhysicalMemorySize() >> 20;
        long freeMemory = osBean.getFreePhysicalMemorySize() >> 20;
        long usedMemory = totalMemory - freeMemory;
        update(cpuLoad, runtimeTotalMemory, runtimeFreeMemory, runtimeUsedMemory, committedHeapMemoryUse,
                usedHeapMemoryUse, maxHeapMemoryUse, committedHeapMemoryUseNonHeap, usedHeapMemoryUseNonHeap, maxHeapMemoryUseNonHeap,
                totalMemory, freeMemory, usedMemory);
    }
    public void update(double cpuLoad, long runtimeTotalMemory, long runtimeFreeMemory, long runtimeUsedMemory, long committedHeapMemoryUse, long usedHeapMemoryUse, long maxHeapMemoryUse, long committedHeapMemoryUseNonHeap, long usedHeapMemoryUseNonHeap, long maxHeapMemoryUseNonHeap, long totalMemory, long freeMemory, long usedMemory) {
        this.cpuLoad = cpuLoad;
        this.runtimeTotalMemory = runtimeTotalMemory;
        this.runtimeFreeMemory = runtimeFreeMemory;
        this.runtimeUsedMemory = runtimeUsedMemory;
        this.committedHeapMemoryUse = committedHeapMemoryUse;
        this.usedHeapMemoryUse = usedHeapMemoryUse;
        this.maxHeapMemoryUse = maxHeapMemoryUse;
        this.committedHeapMemoryUseNonHeap = committedHeapMemoryUseNonHeap;
        this.usedHeapMemoryUseNonHeap = usedHeapMemoryUseNonHeap;
        this.maxHeapMemoryUseNonHeap = maxHeapMemoryUseNonHeap;
        this.totalMemory = totalMemory;
        this.freeMemory = freeMemory;
        this.usedMemory = usedMemory;
    }
}
