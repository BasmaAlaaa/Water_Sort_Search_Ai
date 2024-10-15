package code;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

public class CPUUtilizationTracker {

    public static long getCPUTime() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        return osBean.getProcessCpuTime(); 
    }

    public static void trackCPUUtilization(long startCpuTime, long endCpuTime, long startWallTime, long endWallTime) {
        long cpuTimeUsed = endCpuTime - startCpuTime;  
        long wallTimeUsed = endWallTime - startWallTime;  
        int availableProcessors = Runtime.getRuntime().availableProcessors();

        double cpuUsagePercentage = ((double) cpuTimeUsed / (double) wallTimeUsed) * 100.0 / availableProcessors;

        System.out.println("Normalized CPU Utilization: " + cpuUsagePercentage + "%");
    }
}
