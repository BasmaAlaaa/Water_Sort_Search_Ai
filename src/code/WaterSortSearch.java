package code;

import java.util.*;

public class WaterSortSearch extends GenericSearch {

  private List<Bottle> initialState;

  public WaterSortSearch(String initialStateString) {
    this.initialState = parseInitialState(initialStateString);
  }

  private List<Bottle> parseInitialState(String initialStateString) {
    String[] parts = initialStateString.split(";");
    int numberOfBottles = Integer.parseInt(parts[0]);
    int bottleCapacity = Integer.parseInt(parts[1]);

    List<Bottle> bottles = new ArrayList<>();
    for (int i = 0; i < numberOfBottles; i++) {
      String[] colors = parts[2 + i].split(",");

      List<String> reversedColors = Arrays.asList(colors);
      Collections.reverse(reversedColors); 

      Bottle bottle = new Bottle(bottleCapacity);
      for (String color : reversedColors) {
        if (!color.equals("e")) { 
          bottle.addLayer(color); 
        }
      }
      bottles.add(bottle);
    }
    return bottles;
  }

  @Override
  public boolean isGoalState(Node node) {
    List<Bottle> bottles = node.getState();
    for (Bottle bottle : bottles) {
      if (!isBottleSorted(bottle)) {
        return false;
      }
    }
    return true;
  }

  private boolean isBottleSorted(Bottle bottle) {
    if (bottle.isEmpty()) {
      return true; 
    }

    String topColor = bottle.topLayer();
    Stack<String> layers = bottle.getLayers();

    if (layers.size() == bottle.getCapacity()) { 
      for (String layer : layers) {
        if (!layer.equals(topColor)) {
          return false;
        }
      }
      return true; 
    }

    return false;
  }

  @Override
  public Node getInitialState() {
    return new Node(initialState, null, null, 0, 0);
  }

  
  @Override
  public List<Node> expandNode(Node node) {
    List<Node> children = new ArrayList<>();
    List<Bottle> currentState = node.getState();

    incrementNodesExpanded();

    for (int i = 0; i < currentState.size(); i++) {
        for (int j = 0; j < currentState.size(); j++) {
            if (i != j && isValidAction(currentState.get(i), currentState.get(j))) {
                // Shallow copy the current state
                List<Bottle> newState = new ArrayList<>(currentState);

                // Deep copy only the bottles involved in the action
                Bottle newBottleI = new Bottle(currentState.get(i).emptySpaces() + currentState.get(i).getLayers().size());
                for (String layer : currentState.get(i).getLayers()) {
                    newBottleI.addLayer(layer);
                }
                Bottle newBottleJ = new Bottle(currentState.get(j).emptySpaces() + currentState.get(j).getLayers().size());
                for (String layer : currentState.get(j).getLayers()) {
                    newBottleJ.addLayer(layer);
                }

                newState.set(i, newBottleI);
                newState.set(j, newBottleJ);

                int layers = newState.get(i).pourInto(newState.get(j));

                String action = "pour_" + i + "_" + j;
                Node child = new Node(newState, node, action, node.getPathCost() + layers, node.getHeuristic()); // 3adely path cost hena
                children.add(child);
            }
        }
    }
    return children;
}

  private boolean isValidAction(Bottle from, Bottle to) {
    return !from.isEmpty() && (to.isEmpty() || from.topLayer().equals(to.topLayer())) && !to.isFull();
  }

  @Override
  protected int getNodePriority(Node node, String strategy) {
    double heuristicValue = 0;
    List<Bottle> bottles = node.getState();

    switch (strategy) {
      case "UC":
        return node.getPathCost(); // UCS uses only path cost (g(n))
      case "GR1":
      case "AS1":
        heuristicValue = calculateMisplacedLayersHeuristic(bottles);
        break;
      case "GR2":
      case "AS2":
        heuristicValue = calculateNonHomogeneousHeuristic(bottles);
        break;
      default:
        throw new IllegalArgumentException("Invalid informed search strategy: " + strategy);
    }

    if (strategy.startsWith("GR")) {
      return (int) heuristicValue; // Greedy uses only the heuristic
    } else if (strategy.startsWith("AS")) {
      return node.getPathCost() + (int) heuristicValue; // A* uses path cost + heuristic
    } else {
      throw new IllegalArgumentException("Unsupported strategy for node priority.");
    }
  }

  // First Heuristic: Number of Misplaced Layers
  private double calculateMisplacedLayersHeuristic(List<Bottle> bottles) {
    int misplacedLayers = 0;

    for (Bottle bottle : bottles) {
      if (bottle.isEmpty()) {
        continue;
      }

      String topLayer = bottle.topLayer();
      Stack<String> layers = bottle.getLayers();

      for (int i = 1; i < layers.size(); i++) {
        if (!layers.get(i).equals(topLayer) && !layers.get(i).equals("e")) {
          misplacedLayers++;
        }
      }
    }
    return misplacedLayers;
  }

  // Second Heuristic: Number of Non-Homogeneous Bottles
  private double calculateNonHomogeneousHeuristic(List<Bottle> bottles) {
    int nonHomogeneousCount = 0;

    for (Bottle bottle : bottles) {
      if (bottle.isEmpty()) {
        continue;
      }

      Stack<String> layers = bottle.getLayers();
      String topLayer = bottle.topLayer();
      boolean isHomogeneous = true;

      for (int i = 1; i < layers.size(); i++) {
        if (!layers.get(i).equals(topLayer)) {
          isHomogeneous = false;
          break;
        }
      }

      if (!isHomogeneous) {
        nonHomogeneousCount++;
      }
    }

    return nonHomogeneousCount;
  }

  public static String solve(String initialState, String strategy, boolean visualize) {
    WaterSortSearch searchProblem = new WaterSortSearch(initialState);
    Node solutionNode = searchProblem.search(strategy);

    if (solutionNode == null) {
      return "NOSOLUTION";
    }

    String plan = solutionNode.getSolutionPath();
    int pathCost = solutionNode.getPathCost();
    int nodesExpanded = getNodesExpanded(); 
    if (visualize) {
      visualizeSolution(solutionNode);
    }

    return String.format("%s;%d;%d", plan, pathCost, nodesExpanded);
  }

  private static void visualizeSolution(Node node) {
    Node currentNode = node;
    Stack<Node> path = new Stack<>();

    while (currentNode != null) {
      path.push(currentNode);
      currentNode = currentNode.getParent();
    }

    System.out.println("Solution Visualization:");
    while (!path.isEmpty()) {
      Node n = path.pop();
      if (n.getAction() != null) {
        System.out.println("Action: " + n.getAction());
      }
      for (Bottle bottle : n.getState()) {
        bottle.printBottle(); 
      }
      System.out.println("----------------------");
    }
  }

  private static int nodesExpanded = 0;

  private static void incrementNodesExpanded() {
    nodesExpanded++;
  }

  private static int getNodesExpanded() {
    return nodesExpanded;
  }
}
