package code;
import java.util.*;

public abstract class GenericSearch {

    // Abstract method that each search problem will implement to generate a goal state check
    public abstract boolean isGoalState(Node node);

    // Abstract method to return the initial state as a Node
    public abstract Node getInitialState();

    // Abstract method to define how to expand a node into child nodes (specific to the problem)
    public abstract List<Node> expandNode(Node node);

    // Method to perform a generic search using different strategies
    public Node search(String strategy) {
        // Frontier (nodes to be explored) depending on the strategy
        Queue<Node> frontier;
        switch (strategy) {
            case "BF": // Breadth-First Search
                frontier = new LinkedList<>();
                break;
            case "DF": // Depth-First Search
                frontier = new ArrayDeque<>(); // Stack-like behavior for DFS
                break;
            case "ID": // Iterative Deepening Search
                return iterativeDeepeningSearch();
            case "UC": // Uniform-Cost Search
            case "GR1": // Greedy Search Heuristic 1
            case "GR2": // Greedy Search Heuristic 2
            case "AS1": // A* Search with Heuristic 1
            case "AS2": // A* Search with Heuristic 2
                return informedSearch(strategy);
            default:
                throw new IllegalArgumentException("Invalid strategy: " + strategy);
        }

        Set<Node> explored = new HashSet<>(); // Explored set to avoid cycles
        frontier.add(getInitialState());

        while (!frontier.isEmpty()) {
            Node node = frontier.poll(); // Get the next node to explore

            // If the goal is found, return the solution path
            if (isGoalState(node)) {
                return node;
            }

            // Expand the node and add its children to the frontier
            explored.add(node);
            for (Node child : expandNode(node)) {
                if (!explored.contains(child)) {
                    frontier.add(child);
                }
            }
        }
        return null; // No solution found
    }

    // Uniform Cost, Greedy, and A* search strategies use a priority queue (informed search)
    private Node informedSearch(String strategy) {
        PriorityQueue<Node> frontier = new PriorityQueue<>(Comparator.comparingInt(this::getNodePriority));
        Set<Node> explored = new HashSet<>();
        frontier.add(getInitialState());

        while (!frontier.isEmpty()) {
            Node node = frontier.poll();

            if (isGoalState(node)) {
                return node;
            }

            explored.add(node);
            for (Node child : expandNode(node)) {
                if (!explored.contains(child)) {
                    frontier.add(child);
                }
            }
        }
        return null; // No solution found
    }

    // Iterative Deepening Search (ID)
    private Node iterativeDeepeningSearch() {
        for (int depthLimit = 0; ; depthLimit++) {
            Node result = depthLimitedSearch(getInitialState(), depthLimit);
            if (result != null) {
                return result;
            }
        }
    }

    // Depth-Limited Search helper for ID
    private Node depthLimitedSearch(Node node, int depthLimit) {
        if (isGoalState(node)) {
            return node;
        }
        if (node.getDepth() >= depthLimit) {
            return null;
        }
        for (Node child : expandNode(node)) {
            Node result = depthLimitedSearch(child, depthLimit);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    // Heuristic function for priority queue, based on the search strategy
    private int getNodePriority(Node node) {
        // Return the node's cost depending on the strategy (dummy for now)
        return node.getPathCost(); // You can implement the actual heuristic here
    }
}
