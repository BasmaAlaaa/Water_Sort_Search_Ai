public class Node {
    // Attributes of a node
    private String state; // Encodes the current configuration of bottles and layers
    private Node parent; // Parent node in the search tree
    private int depth;
    private double pathCost; // g(n): Cost to reach this node from the initial state
    private double heuristic; // h(n): Estimated cost to reach the goal from this node

    // Constructor
    public Node(String state, Node parent, int depth, double pathCost, double heuristic) {
        this.state = state;
        this.parent = parent;
        this.depth = depth;
        this.pathCost = pathCost;
        this.heuristic = heuristic;
    }

    // Getters and setters
    public String getState() { return state; }
    public Node getParent() { return parent; }
    public double getPathCost() { return pathCost; }
    public double getHeuristic() { return heuristic; }
    public int getDepth() { return depth; }

    // Method to get the total cost for A* search
    public double getTotalCost() { return pathCost + heuristic; }

    // Overriding equals and hashCode for proper comparison and state handling
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return state.equals(node.state);
    }

    @Override
    public int hashCode() {
        return state.hashCode();
    }
}
