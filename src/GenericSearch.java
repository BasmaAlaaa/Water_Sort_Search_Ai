import java.util.*;

public abstract class GenericSearch {
    // Search strategies use the generic search framework defined here

    // Method to check if a given state is a goal
    public abstract boolean goalTest(String state);

    // Method to generate possible successors for a given state
    public abstract List<Node> generateSuccessors(Node node);

    // Method to implement a search strategy based on a queuing function
    public String search(Node initialNode, String strategy) {
        // Initialize data structures based on the strategy
        Queue<Node> frontier;
        Set<String> explored = new HashSet<>();
        int nodesExpanded = 0;

        switch (strategy) {
            case "BF": // Breadth-First Search
                frontier = new LinkedList<>();
                break;
            case "DF": // Depth-First Search
                frontier = new ArrayDeque<>();
                break;
            case "UC": // Uniform Cost Search
            case "GR1":
            case "GR2":
            case "AS1":
            case "AS2": 
                // Priority Queue for cost-based search strategies
                frontier = new PriorityQueue<>(Comparator.comparing(Node::getTotalCost));
                break;
            default:
                throw new IllegalArgumentException("Invalid strategy provided!");
        }

        // Add the initial node to the frontier
        frontier.add(initialNode);

        while (!frontier.isEmpty()) {
            Node currentNode = frontier.poll();
            nodesExpanded++;
            explored.add(currentNode.getState());

            if (goalTest(currentNode.getState())) {
                return constructSolution(currentNode, nodesExpanded);
            }

            for (Node successor : generateSuccessors(currentNode)) {
                if (!explored.contains(successor.getState()) && !frontier.contains(successor)) {
                    frontier.add(successor);
                }
            }
        }

        return "NOSOLUTION";
    }

    // Method to construct the solution path once a goal is found
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
