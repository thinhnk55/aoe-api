package vn.vietdefi.util.log;

import com.sun.management.OperatingSystemMXBean;
import org.junit.platform.commons.util.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;

public class DebugLogger {
    private static final Logger logger = LoggerFactory.getLogger("debug");

    public static void debug(String data) {
        logger.debug(data);
    }

    public static void debug(String data, Object... objects) {
        logger.debug(data, objects);
    }

    public static void info(String data) {
        logger.info(data);
    }

    public static void info(String data, Object... objects) {
        logger.info(data, objects);
    }

    public static void error(String data) {
        logger.error(data);
    }

    public static void error(String data, Object... objects) {
        logger.error(data, objects);
    }

    public static void debugServerRuntimeState() {
        try {
            double cpuLoad =
                    ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getProcessCpuLoad() * (double) 100;
            Runtime runtime = Runtime.getRuntime();

            long runtime_totalMemory = runtime.totalMemory() >> 20;
            long runtime_freeMemory = runtime.freeMemory() >> 20;

            long runtime_usedMemory = runtime_totalMemory - runtime_freeMemory;

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

            DebugLogger.info("Server resource State: ");
            DebugLogger.info("-------------- Total memory: {} Mb", totalMemory);
            DebugLogger.info("-------------- Free memory: {} Mb", freeMemory);
            DebugLogger.info("-------------- Used memory: {} Mb", usedMemory);
            DebugLogger.info("-------------- CPU: {} %", cpuLoad);
            DebugLogger.info("-------------- runtime: total memory: {} Mb", runtime_totalMemory);
            DebugLogger.info("-------------- runtime: free memory: {} Mb", runtime_freeMemory);
            DebugLogger.info("-------------- runtime: used memory: {} - {} = {} Mb", runtime_totalMemory, runtime_freeMemory, runtime_usedMemory);
            DebugLogger.info("------------------------------------------------------");
            DebugLogger.info("-------------- heap: max memory: {} Mb", maxHeapMemoryUse);
            DebugLogger.info("-------------- heap: used memory: {} Mb", usedHeapMemoryUse);
            DebugLogger.info("-------------- heap: committed memory: {} Mb", committedHeapMemoryUse);
            DebugLogger.info("------------------------------------------------------");
            DebugLogger.info("-------------- non-heap: max memory: {} Mb", maxHeapMemoryUseNonHeap);
            DebugLogger.info("-------------- non-heap: used memory: {} Mb", usedHeapMemoryUseNonHeap);
            DebugLogger.info("-------------- non-heap: committed memory: {} Mb", committedHeapMemoryUseNonHeap);
        } catch (Exception e) {
            DebugLogger.error(ExceptionUtils.readStackTrace(e));
        }
    }
}
