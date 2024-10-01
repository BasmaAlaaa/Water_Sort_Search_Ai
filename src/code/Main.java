package code;
public class Main {
  public static void main(String[] args) {
    String initialState = "5;4;b,y,r,b;b,y,r,r;y,r,b,y;e,e,e,e;e,e,e,e";
    String strategy = "BF";
    WaterSortSearch puzzle = new WaterSortSearch();
    String result = puzzle.solve(initialState, strategy, true);
    System.out.println("Result: " + result);
  }
}