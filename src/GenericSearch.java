import java.util.*;

public abstract class GenericSearch {
    // Method to check if a given state is a goal
    public abstract boolean goalTest(String state);

    // Method to generate possible successors for a given state
    public abstract List<Node> generateSuccessors(Node node);

    // Method to implement a search strategy based on a queuing function
    public String search(Node initialNode, String strategy) {
        // Initialize nodes (the frontier)
        Queue<Node> nodes;

        // Choose the data structure for nodes based on the strategy
        switch (strategy) {
            case "BF": // Breadth-First Search
                nodes = new LinkedList<>(); // FIFO Queue
                break;
            case "DF": // Depth-First Search
                nodes = new ArrayDeque<>(); // LIFO Stack
                break;
            case "UC": // Uniform Cost Search
            case "GR1": // Greedy Search with Heuristic 1
            case "GR2": // Greedy Search with Heuristic 2
            case "AS1": // A* Search with Heuristic 1
            case "AS2": // A* Search with Heuristic 2
                // Priority Queue for cost-based search strategies
                nodes = new PriorityQueue<>(Comparator.comparing(Node::getTotalCost));
                break;
            default:
                throw new IllegalArgumentException("Invalid strategy provided!");
        }

        // Add the initial node to the frontier
        nodes.add(initialNode);
        Set<String> explored = new HashSet<>();
        int nodesExpanded = 0;

        while (!nodes.isEmpty()) {
            Node currentNode = nodes.poll(); // REMOVE-FRONT(nodes)
            nodesExpanded++;

            if (goalTest(currentNode.getState())) {
                return constructSolution(currentNode, nodesExpanded); // If goal is found, return the solution
            }

            // Expand the current node
            explored.add(currentNode.getState());
            List<Node> successors = generateSuccessors(currentNode);

            // Add successors to the queue based on the queuing function
            for (Node successor : successors) {
                if (!explored.contains(successor.getState()) && !nodes.contains(successor)) {
                    nodes.add(successor);
                }
            }
        }

        return "NOSOLUTION"; // If no solution is found, return failure
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
