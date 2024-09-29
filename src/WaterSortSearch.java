import java.util.*;

public class WaterSortSearch extends GenericSearch {

  // Implement the abstract method for generating successors specific to the
  // water-sort puzzle
  @Override
  public List<Node> generateSuccessors(Node node) {
    List<Node> successors = new ArrayList<>();
    String currentState = node.getState();

    // Parse the current state to get information about bottles and layers
    String[] stateParts = currentState.split(";");
    int numberOfBottles = Integer.parseInt(stateParts[0]);
    int bottleCapacity = Integer.parseInt(stateParts[1]);

    // Convert each bottle's contents into a stack representation for easier
    // manipulation
    List<Stack<Character>> bottles = new ArrayList<>();
    for (int i = 2; i < stateParts.length; i++) {
      Stack<Character> bottle = new Stack<>();
      String[] layers = stateParts[i].split(","); // Split by comma to get each color layer
      for (int j = layers.length - 1; j >= 0; j--) {
        bottle.push(layers[j].charAt(0)); // Push the first character of the color (assuming 'b' for blue, etc.)
      }
      bottles.add(bottle);
    }
    System.out.println("bottles: " + bottles);

    // Try to pour from every bottle to every other bottle
    for (int i = 0; i < numberOfBottles; i++) {
      for (int j = 0; j < numberOfBottles; j++) {
        if (i != j && canPour(bottles.get(i), bottles.get(j), bottleCapacity)) {
          // Create a deep copy of the current state
          System.out.println("can pour from " + i + "to " + j);
          List<Stack<Character>> newBottles = deepCopyBottles(bottles);

          // Perform the pour operation
          pour(newBottles.get(i), newBottles.get(j), bottleCapacity);

          // Generate the new state string
          String newState = generateStateString(numberOfBottles, bottleCapacity, newBottles);

          // Calculate the new path cost (increment by the number of layers poured)
          double newPathCost = node.getPathCost() + calculatePourCost(bottles.get(i), newBottles.get(j));
          double heuristic = calculateHeuristic(newState); // Implement your heuristic here

          // Create a new successor node and add it to the list
          Node successor = new Node(newState, node, node.getDepth() + 1, newPathCost, heuristic);
          successors.add(successor);
        }
      }
    }

    return successors;
  }

  // Helper method to check if pouring from bottle i to bottle j is allowed
  private boolean canPour(Stack<Character> fromBottle, Stack<Character> toBottle, int bottleCapacity) {
    System.out.println("fromBottle: " + fromBottle);
    System.out.println("toBottle: " + toBottle);

    // Check if the fromBottle is truly empty (contains only 'e')
    if (isTrulyEmpty(fromBottle)) {
      System.out.println("return false - fromBottle is empty");
      return false; // Cannot pour if fromBottle is empty
    }

    // Check if the toBottle is full
    if (toBottle.size() == bottleCapacity && !isTrulyEmpty(toBottle)) {
      System.out.println("return false - toBottle is full");
      return false; // Cannot pour if toBottle is full and not entirely 'e'
    }

    // If the toBottle is completely empty (filled with 'e'), we can pour into it
    if (isTrulyEmpty(toBottle)) {
      System.out.println("return true - toBottle is empty");
      return true; // Can always pour into an empty bottle
    }

    // Can pour if the top layers have the same color
    if (fromBottle.peek().equals(toBottle.peek())) {
      System.out.println("return true - can pour");
      return true;
    }

    System.out.println("return false - top colors don't match");
    return false; // Cannot pour if the top colors don't match
  }

  // Helper method to check if a bottle contains only 'e' (empty layers)
  private boolean isTrulyEmpty(Stack<Character> bottle) {
    for (char layer : bottle) {
      if (layer != 'e') {
        return false; // Not empty if there's any non-'e' element
      }
    }
    return true; // All layers are 'e', so it's empty
  }

  // Helper method to perform the pour operation from one bottle to another
  private void pour(Stack<Character> fromBottle, Stack<Character> toBottle, int bottleCapacity) {
    char colorToPour = fromBottle.peek();

    // Ensure we don't pour 'e' from fromBottle
    if (colorToPour == 'e') {
      return; // Nothing to pour if the top layer is empty ('e')
    }

    int layersToPour = 0;

    // Count how many layers of the same color are on top in the fromBottle
    while (!fromBottle.isEmpty() && fromBottle.peek() == colorToPour) {
      layersToPour++;
      fromBottle.pop(); // Remove the top layer from fromBottle
    }

    // Only pour as many layers as there is space in the toBottle
    int actualLayersToPour = Math.min(layersToPour, bottleCapacity - toBottle.size());
    for (int i = 0; i < actualLayersToPour; i++) {
      toBottle.push(colorToPour);
    }

    // If there is still space in the toBottle, fill with 'e' (empty)
    while (toBottle.size() < bottleCapacity) {
      toBottle.push('e');
    }
  }

  // Helper method to generate the state string from a list of bottles
  private String generateStateString(int numberOfBottles, int bottleCapacity, List<Stack<Character>> bottles) {
    StringBuilder stateBuilder = new StringBuilder();

    // Add the number of bottles and bottle capacity as the first two parts of the
    // state
    stateBuilder.append(numberOfBottles).append(";").append(bottleCapacity).append(";");

    // Add the content of each bottle
    for (int i = 0; i < bottles.size(); i++) {
      if (i > 0) {
        stateBuilder.append(';');
      }
      Stack<Character> bottle = bottles.get(i);

      // Add actual layers, if there are empty slots, fill them with 'e'
      for (int j = bottle.size() - 1; j >= 0; j--) {
        stateBuilder.append(bottle.get(j));
      }
      // Fill remaining spaces in bottle with 'e' if necessary
      for (int j = bottle.size(); j < bottleCapacity; j++) {
        stateBuilder.append('e');
      }
    }

    return stateBuilder.toString();
  }

  // Helper method to create a deep copy of the bottles
  private List<Stack<Character>> deepCopyBottles(List<Stack<Character>> bottles) {
    List<Stack<Character>> newBottles = new ArrayList<>();
    for (Stack<Character> bottle : bottles) {
      newBottles.add(new Stack<Character>() {
        {
          addAll(bottle);
        }
      });
    }
    return newBottles;
  }

  // Helper method to calculate the cost of the pour (number of layers poured)
  private double calculatePourCost(Stack<Character> fromBottle, Stack<Character> toBottle) {
    if (fromBottle.isEmpty() || fromBottle.peek() == 'e') {
      return 0; // Nothing to pour if fromBottle is empty or top layer is 'e'
    }

    char colorToPour = fromBottle.peek();
    int layersToPour = 0;

    // Count how many layers of the same color are on top without modifying the
    // fromBottle
    for (int i = fromBottle.size() - 1; i >= 0 && fromBottle.get(i) == colorToPour; i--) {
      layersToPour++;
    }

    // Only pour as many layers as there is space in the toBottle
    return Math.min(layersToPour, toBottle.capacity() - toBottle.size());
  }

  // Heuristic: Counts the number of non-homogeneous bottles
  private double calculateHeuristic(String state) {
    // Parse the current state to get information about bottles
    String[] stateParts = state.split(";");
    // int numberOfBottles = Integer.parseInt(stateParts[0]);

    int nonHomogeneousCount = 0;

    // Start checking bottles from the third part of the state (index 2)
    for (int i = 2; i < stateParts.length; i++) {
      String bottle = stateParts[i];

      if (bottle.isEmpty()) {
        continue; // Empty bottles are considered homogeneous
      }

      // Check if all layers in the bottle are of the same color
      char firstColor = bottle.charAt(0);
      boolean isHomogeneous = true;

      for (int j = 1; j < bottle.length(); j++) {
        if (bottle.charAt(j) != firstColor) {
          isHomogeneous = false;
          break;
        }
      }

      // If the bottle is not homogeneous, count it
      if (!isHomogeneous) {
        nonHomogeneousCount++;
      }
    }

    // Return the number of non-homogeneous bottles as the heuristic
    return nonHomogeneousCount;
  }

  @Override
  public boolean goalTest(String state) {
    // Parse the current state to get the information about bottles and their layers
    String[] stateParts = state.split(";");
    System.out.println("stateParts: " + stateParts[0]);

    int numberOfBottles = Integer.parseInt(stateParts[0]);

    for (int i = 2; i < 2 + numberOfBottles; i++) {
      String bottle = stateParts[i];

      if (bottle.isEmpty()) {
        // Empty bottle is considered sorted
        continue;
      }

      // Check if all layers in the bottle are of the same color
      char firstColor = bottle.charAt(0);
      for (int j = 1; j < bottle.length(); j++) {
        if (bottle.charAt(j) != firstColor) {
          return false; // If there's a different color, it's not sorted
        }
      }
    }

    // If all bottles are uniform or empty, return true
    return true;
  }

  // The main solve method to apply the search strategy
  public String solve(String initialState, String strategy, boolean visualize) {
    Node initialNode = new Node(initialState, null, 0, 0, 0);
    return search(initialNode, strategy);
  }
}