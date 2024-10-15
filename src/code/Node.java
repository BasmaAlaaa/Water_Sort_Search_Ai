package code;
import java.util.*;

public class Node {

  private Node parent; // The parent node in the search tree
  private List<Bottle> state; // The state of the puzzle (the current configuration of bottles)
  private String action; // The action that led to this node (e.g., "pour_0_1")
  private int pathCost; // The cost to reach this node from the start node
  private int depth; // Depth of the node in the search tree
  private int heuristic; // Heuristic value for informed search algorithms

  // Constructor
  public Node(List<Bottle> state, Node parent, String action, int pathCost, int heuristic) {
    this.state = state;
    this.parent = parent;
    this.action = action;
    this.pathCost = pathCost;
    this.depth = (parent == null) ? 0 : parent.depth + 1; // Depth is 0 if the node is root
    this.heuristic = heuristic;
  }

  // Getters
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

  // Check if the current node state is a goal state (i.e., all bottles are sorted)
  public boolean isGoalState() {
    for (Bottle bottle : state) {
      if (!isBottleSorted(bottle)) {
        return false;
      }
    }
    return true;
  }

  // Helper function to check if a single bottle is sorted (i.e., all layers are
  // of the same color or empty)
  private boolean isBottleSorted(Bottle bottle) {
    if (bottle.isEmpty())
      return true; // An empty bottle is considered sorted

    String topColor = bottle.topLayer();
    for (String layer : bottle.getLayers()) {
      if (!layer.equals(topColor)) {
        return false;
      }
    }
    return true;
  }

  // Generate the child nodes by applying valid actions (pouring from one bottle
  // to another)
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

  // Helper method to check if pouring from one bottle to another is valid
  private boolean isValidAction(Bottle from, Bottle to) {
    return !from.isEmpty() && (to.isEmpty() || from.topLayer().equals(to.topLayer())) && !to.isFull();
  }

  // Helper method to make a deep copy of the current state (list of bottles)
  private List<Bottle> deepCopyState(List<Bottle> state) {
    List<Bottle> newState = new ArrayList<>();
    for (Bottle bottle : state) {
      Bottle newBottle = new Bottle(bottle.emptySpaces() + bottle.getLayers().size()); // Copy capacity
      for (String layer : bottle.getLayers()) {
        newBottle.addLayer(layer);
      }
      newState.add(newBottle);
    }
    return newState;
  }

  // Trace back the path from this node to the root (to get the solution)
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
