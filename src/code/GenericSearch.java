package code;

import java.util.*;

public abstract class GenericSearch {

    public abstract boolean isGoalState(Node node);

    public abstract Node getInitialState();

    public abstract List<Node> expandNode(Node node);

    protected abstract int getNodePriority(Node node, String strategy);

    public Node search(String strategy) {
        Queue<Node> frontier;
        switch (strategy) {
            case "BF": // Breadth-First Search
                frontier = new LinkedList<>();
                break;
            case "DF": // Depth-First Search
                frontier = new ArrayDeque<>();
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
        return null;
    }

    private Node informedSearch(String strategy) {
        PriorityQueue<Node> frontier = new PriorityQueue<>(
                Comparator.comparingInt(node -> getNodePriority(node, strategy)));
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
        return null;
    }

    private Node iterativeDeepeningSearch() {
        for (int depthLimit = 0;; depthLimit++) {
            Node result = depthLimitedSearch(getInitialState(), depthLimit);
            if (result != null) {
                return result;
            }
        }
    }

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
}
