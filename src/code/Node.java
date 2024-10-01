package code;
public class Node {
  private String state;
  private Node parent;
  private int depth;
  private double pathCost;
  private double heuristic;

  public Node(String state, Node parent, int depth, double pathCost, double heuristic) {
    this.state = state;
    this.parent = parent;
    this.depth = depth;
    this.pathCost = pathCost;
    this.heuristic = heuristic;
  }

  public String getState() {
    return state;
  }

  public Node getParent() {
    return parent;
  }

  public double getPathCost() {
    return pathCost;
  }

  public double getHeuristic() {
    return heuristic;
  }

  public int getDepth() {
    return depth;
  }

  public double getTotalCost() {
    return pathCost + heuristic;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Node node = (Node) o;
    return state.equals(node.state);
  }

  @Override
  public int hashCode() {
    return state.hashCode();
  }
}
