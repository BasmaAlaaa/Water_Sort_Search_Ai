package code;
import java.util.*;

public abstract class GenericSearch {
  public abstract boolean goalTest(String state);

  public abstract List<Node> generateSuccessors(Node node, String strategy);

  public String search(Node initialNode, String strategy) {
    Queue<Node> nodes;

    switch (strategy) {
      case "BF":
        nodes = new LinkedList<>();
        break;
      case "DF":
        nodes = new ArrayDeque<>();
        break;
      case "UC":
      case "GR1":
      case "GR2":
      case "AS1":
      case "AS2":
        nodes = new PriorityQueue<>(Comparator.comparing(Node::getTotalCost));
        break;
      default:
        throw new IllegalArgumentException("Invalid strategy provided!");
    }

    nodes.add(initialNode);
    Set<String> explored = new HashSet<>();
    int nodesExpanded = 0;

    while (!nodes.isEmpty()) {
      Node currentNode = nodes.poll();
      System.out.println("currentNode: " + currentNode.getState());
      nodesExpanded++;

      if (goalTest(currentNode.getState())) {
        return constructSolution(currentNode, nodesExpanded);
      }

      explored.add(currentNode.getState());
      List<Node> successors = generateSuccessors(currentNode, strategy);

      for (Node successor : successors) {
        if (!explored.contains(successor.getState()) && !nodes.contains(successor)) {
          nodes.add(successor);
        }
      }
    }

    return "NOSOLUTION";
  }

  private String constructSolution(Node goalNode, int nodesExpanded) {
    List<String> plan = new ArrayList<>();
    double totalCost = goalNode.getPathCost();

    Node currentNode = goalNode;
    while (currentNode.getParent() != null) { 
      plan.add(0, currentNode.getState());
      currentNode = currentNode.getParent();
    }

    return String.join(",", plan) + ";" + totalCost + ";" + nodesExpanded;
  }
}
