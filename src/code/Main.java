package code;

public class Main {
    public static void main(String[] args) {
        String initialState = "5;4;b,y,r,b;b,y,r,r;y,r,b,y;e,e,e,e;e,e,e,e";
        String grid0 = "3;" +
                "4;" +
                "r,y,r,y;" +
                "y,r,y,r;" +
                "e,e,e,e;";
        String grid1 = "5;" +
                "4;" +
                "b,y,r,b;" +
                "b,y,r,r;" +
                "y,r,b,y;" +
                "e,e,e,e;" +
                "e,e,e,e;";
        String grid2 = "5;" +
                "4;" +
                "b,r,o,b;" +
                "b,r,o,o;" +
                "r,o,b,r;" +
                "e,e,e,e;" +
                "e,e,e,e;";
        String grid3 = "6;" +
                "4;" +
                "g,g,g,r;" +
                "g,y,r,o;" +
                "o,r,o,y;" +
                "y,o,y,b;" +
                "r,b,b,b;" +
                "e,e,e,e;";
        String grid4 = "6;" +
                "3;" +
                "r,r,y;" +
                "b,y,r;" +
                "y,b,g;" +
                "g,g,b;" +
                "e,e,e;" +
                "e,e,e;";

                long startCpuTime = CPUUtilizationTracker.getCPUTime();
                long startWallTime = System.nanoTime();
        
                String result = RAMUsageTracker.trackRAMUsage(() -> {
                    String strategy = "AS2";
                    WaterSortSearch puzzle = new WaterSortSearch(initialState);
                    return puzzle.solve(initialState, strategy, true);
                });
        
                long endCpuTime = CPUUtilizationTracker.getCPUTime();
                long endWallTime = System.nanoTime();        
                CPUUtilizationTracker.trackCPUUtilization(startCpuTime, endCpuTime, startWallTime, endWallTime);

                System.out.println("Result: " + result);
            }
}
