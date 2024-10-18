package code;

import java.util.*;

public class Node {

  private Node parent;
  private List<Bottle> state;
  private String action;
  private int pathCost;
  private int depth;
  private int heuristic;

  public Node(List<Bottle> state, Node parent, String action, int pathCost, int heuristic) {
    this.state = state;
    this.parent = parent;
    this.action = action;
    this.pathCost = pathCost;
    this.depth = (parent == null) ? 0 : parent.depth + 1;
    this.heuristic = heuristic;
  }

  public Node getParent() {
    return parent;
  }

  public List<Bottle> getState() {
    return state;
  }

  public String getAction() {
    return action;
  }

  public int getPathCost() {
    return pathCost;
  }

  public int getDepth() {
    return depth;
  }

  public String getSolutionPath() {
    if (parent == null) {
      return "";
    }
    return parent.getSolutionPath() + action + ",";
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;

    Node node = (Node) obj;

    return state.equals(node.state);
  }

  @Override
  public int hashCode() {
    return state.hashCode();
  }

  public int getHeuristic() {
    return heuristic;
  }

  public void setHeuristic(int heuristic) {
    this.heuristic = heuristic;
  }

  public int getTotalCost() {
    return this.pathCost + this.heuristic;
  }
}