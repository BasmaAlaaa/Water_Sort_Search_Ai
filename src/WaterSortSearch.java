import java.util.*;

public class WaterSortSearch extends GenericSearch {

    // Method to check if a given state is the goal
    @Override
    public boolean goalTest(String state) {
        // Split the state string into parts
        String[] parts = state.split(";");

        // Get the number of bottles (first part) and the bottle capacity (second part)
        int numberOfBottles = Integer.parseInt(parts[0]);
        int bottleCapacity = Integer.parseInt(parts[1]);

        // Iterate over each bottle starting from the third part in the split array
        for (int i = 2; i < numberOfBottles + 2; i++) {
            String bottle = parts[i];
            // Split the bottle into layers
            String[] layers = bottle.split(",");

            // Check if all layers are of the same color or empty
            String topColor = null;
            for (String layer : layers) {
                if (!layer.equals("e")) { // Ignore empty layers
                    if (topColor == null) {
                        topColor = layer; // Set topColor as the first non-empty layer color
                    } else if (!topColor.equals(layer)) {
                        return false; // If any non-empty layer is different, the goal is not met
                    }
                }
            }
        }
        return true; // All bottles meet the goal condition
    }  

    // Method to generate successors from the current node
    @Override
    public List<Node> generateSuccessors(Node node) {
        List<Node> successors = new ArrayList<>();
        String currentState = node.getState();
        String[] parts = currentState.split(";");

        int numberOfBottles = Integer.parseInt(parts[0]);
        int bottleCapacity = Integer.parseInt(parts[1]);

        // Create an array of bottles where each bottle contains its layers
        String[][] bottles = new String[numberOfBottles][bottleCapacity];
        for (int i = 0; i < numberOfBottles; i++) {
            bottles[i] = parts[i + 2].split(",");
        }

        // Generate all possible valid "pour" actions
        for (int from = 0; from < numberOfBottles; from++) {
            for (int to = 0; to < numberOfBottles; to++) {
                if (from != to && canPour(bottles[from], bottles[to])) {
                    // Perform the pour action to generate a new state
                    String[][] newBottles = deepCopyBottles(bottles);
                    pour(newBottles[from], newBottles[to]);

                    // Convert the new bottle configuration to a state string
                    String newState = generateStateString(newBottles);

                    // Ensure we are not revisiting the same state
                    if (!newState.equals(currentState)) {
                        // Create a new Node with the new state
                        Node successor = new Node(newState, node, node.getPathCost() + 1, heuristic(newState));
                        successors.add(successor);

                        // Debugging: Print the generated successor state
                        System.out.println("Generated Successor: " + newState);
                    }
                }
            }
        }
        return successors;
    }

    // Check if you can pour from one bottle to another
    private boolean canPour(String[] fromBottle, String[] toBottle) {
        // Find the top non-empty color in the fromBottle
        String topColorFrom = getTopColor(fromBottle);
        if (topColorFrom.equals("e")) return false; // If fromBottle is empty, can't pour

        // Check if toBottle has space and can accept the top color
        String topColorTo = getTopColor(toBottle);
        if (topColorTo.equals("e")) return true; // If toBottle is empty, you can pour

        // If toBottle is not empty, you can only pour if the top colors match and there's space
        return topColorTo.equals(topColorFrom) && hasSpace(toBottle);
    }

    // Get the topmost non-empty color of a bottle
    private String getTopColor(String[] bottle) {
        for (int i = 0; i < bottle.length; i++) {
            if (!bottle[i].equals("e")) {
                return bottle[i];
            }
        }
        return "e"; // Return "e" if the bottle is empty
    }

    // Check if the bottle has empty spaces
    private boolean hasSpace(String[] bottle) {
        for (String layer : bottle) {
            if (layer.equals("e")) {
                return true; // There is space if any layer is empty
            }
        }
        return false; // No space if all layers are filled
    }

    // Perform the pour action from one bottle to another
    private void pour(String[] fromBottle, String[] toBottle) {
        // Get the top color and the count of that color in fromBottle
        String topColor = getTopColor(fromBottle);
        int pourCount = 0;

        for (int i = 0; i < fromBottle.length && fromBottle[i].equals(topColor); i++) {
            pourCount++;
        }

        // Determine how much can be poured based on space in toBottle
        int spaceLeft = 0;
        for (String layer : toBottle) {
            if (layer.equals("e")) spaceLeft++;
        }

        int transferAmount = Math.min(pourCount, spaceLeft);

        // Pour the liquid
        for (int i = 0; i < transferAmount; i++) {
            // Find the topmost empty space in toBottle
            for (int j = toBottle.length - 1; j >= 0; j--) {
                if (toBottle[j].equals("e")) {
                    toBottle[j] = topColor;
                    break;
                }
            }
            // Remove the top layer from fromBottle
            for (int j = 0; j < fromBottle.length; j++) {
                if (fromBottle[j].equals(topColor)) {
                    fromBottle[j] = "e";
                    break;
                }
            }
        }
    }

    // Helper method to create a deep copy of the bottle array
    private String[][] deepCopyBottles(String[][] bottles) {
        String[][] newBottles = new String[bottles.length][];
        for (int i = 0; i < bottles.length; i++) {
            newBottles[i] = Arrays.copyOf(bottles[i], bottles[i].length);
        }
        return newBottles;
    }

    // Helper method to generate the state string from the bottles configuration
    private String generateStateString(String[][] bottles) {
        StringBuilder stateBuilder = new StringBuilder();
        stateBuilder.append(bottles.length).append(";").append(bottles[0].length).append(";");
        for (String[] bottle : bottles) {
            stateBuilder.append(String.join(",", bottle)).append(";");
        }
        return stateBuilder.toString();
    }

    // Implement a heuristic function to estimate the cost (you can adjust this)
    private double heuristic(String state) {
        // A simple heuristic could be the number of non-homogeneous bottles
        String[] parts = state.split(";");
        int nonHomogeneousCount = 0;
        for (int i = 2; i < parts.length; i++) {
            String[] layers = parts[i].split(",");
            String topColor = null;
            boolean homogeneous = true;
            for (String layer : layers) {
                if (!layer.equals("e")) {
                    if (topColor == null) {
                        topColor = layer;
                    } else if (!topColor.equals(layer)) {
                        homogeneous = false;
                        break;
                    }
                }
            }
            if (!homogeneous) nonHomogeneousCount++;
        }
        return nonHomogeneousCount;
    }

    // Solve method as required by the project
    public static String solve(String initialState, String strategy, boolean visualize) {
        WaterSortSearch waterSortSearch = new WaterSortSearch();
        Node initialNode = new Node(initialState, null, 0, 0); // Initial node with no cost and no heuristic
        
        // Debugging: Print initial state and strategy
        System.out.println("Initial State: " + initialState);
        System.out.println("Strategy: " + strategy);
        
        String solution = waterSortSearch.search(initialNode, strategy);
        
        if (visualize) {
            System.out.println("Solution Steps: " + solution);
        }
        
        return solution;
    }
}
