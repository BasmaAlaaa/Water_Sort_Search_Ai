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

  public boolean isGoalState() {
    for (Bottle bottle : state) {
      if (!isBottleSorted(bottle)) {
        return false;
      }
    }
    return true;
  }

  private boolean isBottleSorted(Bottle bottle) {
    if (bottle.isEmpty())
      return true; 

    String topColor = bottle.topLayer();
    for (String layer : bottle.getLayers()) {
      if (!layer.equals(topColor)) {
        return false;
      }
    }
    return true;
  }

  public List<Node> expand() {
    List<Node> children = new ArrayList<>();

    for (int i = 0; i < state.size(); i++) {
      for (int j = 0; j < state.size(); j++) {
        if (i != j && isValidAction(state.get(i), state.get(j))) {
          List<Bottle> newState = deepCopyState(state);
          newState.get(i).pourInto(newState.get(j));

          String action = "pour_" + i + "_" + j;
          Node child = new Node(newState, this, action, getTotalCost(), this.heuristic);
          children.add(child);
        }
      }
    }
    return children;
  }

  private boolean isValidAction(Bottle from, Bottle to) {
    return !from.isEmpty() && (to.isEmpty() || from.topLayer().equals(to.topLayer())) && !to.isFull();
  }

  private List<Bottle> deepCopyState(List<Bottle> state) {
    List<Bottle> newState = new ArrayList<>();
    for (Bottle bottle : state) {
      Bottle newBottle = new Bottle(bottle.emptySpaces() + bottle.getLayers().size());
      for (String layer : bottle.getLayers()) {
        newBottle.addLayer(layer);
      }
      newState.add(newBottle);
    }
    return newState;
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
