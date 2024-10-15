package code;

import java.util.function.Supplier;

public class RAMUsageTracker {

    public static String trackRAMUsage(Supplier<String> searchAlgorithm) {
        Runtime runtime = Runtime.getRuntime();

        long beforeUsedMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used Memory Before Execution: " + (beforeUsedMemory / (1024 * 1024)) + " MB");

        long startTime = System.nanoTime();
        String result = searchAlgorithm.get();  
        long endTime = System.nanoTime();
        long durationInNano = endTime - startTime;
        System.out.println("Execution Time: " + (durationInNano / 1_000_000) + " ms");

        long afterUsedMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used Memory After Execution: " + (afterUsedMemory / (1024 * 1024)) + " MB");

        long memoryDifference = afterUsedMemory - beforeUsedMemory;
        System.out.println("Memory Used During Execution: " + (memoryDifference / (1024 * 1024)) + " MB");

        return result;
    }
}
