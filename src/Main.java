public class Main {
    public static void main(String[] args) {
        // String initialState = "5;4;" +
        //         "b,y,r,b;" +
        //         "b,y,r,r;" +
        //         "y,r,b,y;" +
        //         "e,e,e,e;" +
        //         "e,e,e,e;";

        // String strategy = "BF"; // Use different strategies for testing

        // String result = WaterSortSearch.solve(initialState, strategy, true);
        // System.out.println("Result: " + result);
        String initialState = "3;3;r,r,r;e,e,e;e,e,e;"; // Simple test case for DFS
String result = WaterSortSearch.solve(initialState, "DF", true);
System.out.println("DFS Result: " + result);

    }
}
