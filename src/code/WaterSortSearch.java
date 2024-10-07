package code;
import java.util.*;

public class WaterSortSearch extends GenericSearch {

    private List<Bottle> initialState;

    // Constructor that initializes the water sort puzzle with the initial state (a list of bottles)
    public WaterSortSearch(String initialStateString) {
        this.initialState = parseInitialState(initialStateString);
    }

    // Parses the initial state string and creates a list of Bottle objects
    private List<Bottle> parseInitialState(String initialStateString) {
        String[] parts = initialStateString.split(";");
        int numberOfBottles = Integer.parseInt(parts[0]);
        int bottleCapacity = Integer.parseInt(parts[1]);

        List<Bottle> bottles = new ArrayList<>();
        for (int i = 0; i < numberOfBottles; i++) {
            String[] colors = parts[2 + i].split(",");
            Bottle bottle = new Bottle(bottleCapacity);
            for (String color : colors) {
                if (!color.equals("e")) { // 'e' represents an empty layer
                    bottle.addLayer(color);
                }
            }
            bottles.add(bottle);
        }
        return bottles;
    }

    // Check if the node represents the goal state
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

    // Helper method to check if a single bottle is sorted (i.e., all layers are of the same color or empty)
    private boolean isBottleSorted(Bottle bottle) {
        if (bottle.isEmpty()) return true;
        String topColor = bottle.topLayer();
        for (String layer : bottle.getLayers()) {
            if (!layer.equals(topColor)) {
                return false;
            }
        }
        return true;
    }

    // Return the initial state as a Node
    @Override
    public Node getInitialState() {
        return new Node(initialState, null, null, 0);
    }

    // Expands the current node into its possible children (all valid pour actions)
    @Override
    public List<Node> expandNode(Node node) {
        List<Node> children = new ArrayList<>();
        List<Bottle> currentState = node.getState();

        for (int i = 0; i < currentState.size(); i++) {
            for (int j = 0; j < currentState.size(); j++) {
                if (i != j && isValidAction(currentState.get(i), currentState.get(j))) {
                    List<Bottle> newState = deepCopyState(currentState);
                    newState.get(i).pourInto(newState.get(j));

                    String action = "pour_" + i + "_" + j;
                    Node child = new Node(newState, node, action, node.getPathCost() + 1);
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

    // Helper method to create a deep copy of the current state (list of bottles)
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

    // Solve method as described in the project requirements
    public String solve(String initialState, String strategy, boolean visualize) {
        WaterSortSearch searchProblem = new WaterSortSearch(initialState);
        Node solutionNode = searchProblem.search(strategy);

        if (solutionNode == null) {
            return "NOSOLUTION";
        }

        String plan = solutionNode.getSolutionPath();
        int pathCost = solutionNode.getPathCost();
        int nodesExpanded = getNodesExpanded(); // Implement this based on your expansion count

        if (visualize) {
            visualizeSolution(solutionNode);
        }

        return String.format("%s;%d;%d", plan, pathCost, nodesExpanded);
    }

    // Helper method to visualize the solution (prints to console for debugging)
    private void visualizeSolution(Node node) {
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
                bottle.printBottle(); // Use the printBottle method to visualize the state of each bottle
            }
            System.out.println("----------------------");
        }
    }

    // Helper method to track nodes expanded (increment this during expansion)
    private int nodesExpanded = 0;

    private void incrementNodesExpanded() {
        nodesExpanded++;
    }

    private int getNodesExpanded() {
        return nodesExpanded;
    }
}
