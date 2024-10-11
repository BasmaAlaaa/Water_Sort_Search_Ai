package code;

import java.util.*;

public class WaterSortSearch extends GenericSearch {

  // Implement the abstract method for generating successors specific to the
  // water-sort puzzle
  @Override
  public List<Node> generateSuccessors(Node node, String strategy) {
    List<Node> successors = new ArrayList<>();
    String currentState = node.getState();

    // Parse the current state to get information about bottles and layers
    String[] stateParts = currentState.split(";");
    int numberOfBottles = Integer.parseInt(stateParts[0]);
    int bottleCapacity = Integer.parseInt(stateParts[1]);

    // Ensure that the stateParts array has the correct length
    if (stateParts.length != 2 + numberOfBottles) {
      System.out.println("State Parts Length: " + stateParts.length);
      throw new IllegalStateException("State string does not match expected format.");
    }

    // Convert each bottle's contents into a Bottle object for easier manipulation
    List<Bottle> bottles = new ArrayList<>();
    for (int i = 2; i < 2 + numberOfBottles; i++) {
      Bottle bottle = new Bottle(bottleCapacity);
      String bottleStr = stateParts[i];
      if (!bottleStr.isEmpty()) {
        String[] layers = bottleStr.split(","); // Split by comma to get each color layer
        for (int j = layers.length - 1; j >= 0; j--) {
          if (!layers[j].equals("e"))
            bottle.addLayer(layers[j].charAt(0)); // Add the first character of the color
        }
      }
      bottles.add(bottle);
    }

    // Explore all possible pour actions from each bottle to every other bottle
    for (int i = 0; i < numberOfBottles; i++) {
      for (int j = 0; j < numberOfBottles; j++) {
        if (i != j && canPour(bottles.get(i), bottles.get(j))) {

          // Create a deep copy of the current state
          List<Bottle> newBottles = deepCopyBottles(bottles);
          System.out.println("Before Pour - From Bottle: " + newBottles.get(i).getLayers() + " To Bottle: "
              + newBottles.get(i).getLayers());

          // Perform the pour operation
          pour(newBottles.get(i), newBottles.get(j));

          System.out.println("After Pour - From Bottle: " + newBottles.get(i).getLayers() + " To Bottle: "
              + newBottles.get(i).getLayers());

          // Generate the new state string
          String newState = generateStateString(numberOfBottles, bottleCapacity, newBottles);

          // Calculate the new path cost (increment by the number of layers poured)
          double newPathCost = node.getPathCost() + calculatePourCost(bottles.get(i), bottles.get(j));

          double heuristic;
          if (strategy.equals("GR1") || strategy.equals("AS1")) {
            heuristic = calculateMisplacedLayersHeuristic(newState); // First heuristic
          } else {
            heuristic = calculateNonHomogeneousHeuristic(newState); // Second heuristic
          }

          // Create a new successor node and add it to the list
          Node successor = new Node(newState, node, node.getDepth() + 1, newPathCost, heuristic);
          successors.add(successor);
        }
      }
    }

    return successors;
  }

  // Helper method to check if pouring from bottle i to bottle j is allowed
  private boolean canPour(Bottle fromBottle, Bottle toBottle) {
    // Check if the fromBottle has something to pour
    if (fromBottle.isEmpty()) {
      return false; // Cannot pour if fromBottle is empty
    }

    // Check if the toBottle has space to accept the pour
    if (toBottle.isFull()) {
      return false; // Cannot pour if toBottle is already full
    }

    // Can always pour into an empty bottle
    if (toBottle.isEmpty()) {
      return true;
    }

    // Can pour if the top layers of both bottles match
    return fromBottle.getTopColor() == toBottle.getTopColor();
  }

  // Helper method to perform the pour operation from one bottle to another
  private void pour(Bottle fromBottle, Bottle toBottle) {
    if (fromBottle.isEmpty()) {
      return; // Nothing to pour if the source bottle is empty
    }

    char colorToPour = fromBottle.getTopColor();
    int layersAvailableToPour = 0;

    // Count how many layers of the same color are on top in the fromBottle
    Stack<Character> fromLayers = fromBottle.getLayers();
    for (int i = fromLayers.size() - 1; i >= 0 && fromLayers.get(i) == colorToPour; i--) {
      layersAvailableToPour++;
    }

    // Determine how many layers we can actually pour
    int spaceInToBottle = toBottle.getCapacity() - toBottle.size();
    int actualLayersToPour = Math.min(layersAvailableToPour, spaceInToBottle);

    // Perform the pour
    for (int i = 0; i < actualLayersToPour; i++) {
      toBottle.addLayer(fromBottle.removeLayer()); // This should remove the top layer from fromBottle
    }
  }

  // private void pour(Bottle fromBottle, Bottle toBottle) {
  // char colorToPour = fromBottle.getTopColor();

  // // Count how many layers of the same color are on top in the fromBottle
  // int layersAvailableToPour = 0;
  // Stack<Character> fromLayers = fromBottle.getLayers();
  // for (int i = fromLayers.size() - 1; i >= 0 && fromLayers.get(i) ==
  // colorToPour; i--) {
  // layersAvailableToPour++;
  // }

  // // Determine how many layers we can actually pour
  // int spaceInToBottle = toBottle.getCapacity() - toBottle.size();
  // int actualLayersToPour = Math.min(layersAvailableToPour, spaceInToBottle);

  // // Perform the pour
  // for (int i = 0; i < actualLayersToPour; i++) {
  // toBottle.addLayer(fromBottle.removeLayer());
  // }
  // }

  // Helper method to generate the state string from a list of bottles
  private String generateStateString(int numberOfBottles, int bottleCapacity, List<Bottle> bottles) {
    StringBuilder stateBuilder = new StringBuilder();

    // Add the number of bottles and bottle capacity as the first two parts of the
    // state
    stateBuilder.append(numberOfBottles).append(";").append(bottleCapacity).append(";");

    // Add the content of each bottle
    for (Bottle bottle : bottles) {
      Stack<Character> layers = bottle.getLayers();
      int layerCount = layers.size();

      // Fill empty layers at the bottom
      for (int j = layerCount; j < bottleCapacity; j++) {
        stateBuilder.append("e");
        if (j < bottleCapacity - 1) {
          stateBuilder.append(","); // Add commas between empty layers
        }
      }

      // Add actual layers on top of the empty layers
      for (int j = layers.size() - 1; j >= 0; j--) {
        stateBuilder.append(layers.get(j));
        if (j > 0) {
          stateBuilder.append(","); // Add commas between actual layers
        }
      }

      // Always end with a semicolon for each bottle
      stateBuilder.append(";");
    }

    return stateBuilder.toString();
  }

  // private String generateStateString(int numberOfBottles, int bottleCapacity,
  // List<Bottle> bottles) {
  // StringBuilder stateBuilder = new StringBuilder();

  // // Add the number of bottles and bottle capacity as the first two parts of
  // the
  // // state
  // stateBuilder.append(numberOfBottles).append(";").append(bottleCapacity);

  // // Add the content of each bottle
  // for (Bottle bottle : bottles) {
  // stateBuilder.append(';');
  // Stack<Character> layers = bottle.getLayers();

  // if (!layers.isEmpty()) {
  // // Add actual layers
  // for (int i = layers.size() - 1; i >= 0; i--) {
  // stateBuilder.append(layers.get(i));
  // if (i > 0) {
  // stateBuilder.append(",");
  // }
  // }
  // }
  // }

  // return stateBuilder.toString();
  // }

  // Helper method to create a deep copy of the bottles
  private List<Bottle> deepCopyBottles(List<Bottle> bottles) {
    List<Bottle> newBottles = new ArrayList<>();
    for (Bottle bottle : bottles) {
      newBottles.add(bottle.deepCopy()); // Use Bottle's deepCopy method
    }
    return newBottles;
  }

  // Helper method to calculate the cost of the pour (number of layers poured)
  private double calculatePourCost(Bottle fromBottle, Bottle toBottle) {
    if (fromBottle.isEmpty()) {
      return 0; // Nothing to pour if fromBottle is empty
    }

    char colorToPour = fromBottle.getTopColor();
    int layersAvailableToPour = 0;

    // Count how many layers of the same color are on top without modifying the
    // fromBottle
    Stack<Character> fromLayers = fromBottle.getLayers(); // Get the layers directly from the bottle
    for (int i = fromLayers.size() - 1; i >= 0 && fromLayers.get(i) == colorToPour; i--) {
      layersAvailableToPour++;
    }

    // Only pour as many layers as there is space in the toBottle
    int spaceInToBottle = toBottle.getCapacity() - toBottle.size();
    return Math.min(layersAvailableToPour, spaceInToBottle);
  }

  // First Heuristic: Number of Misplaced Layers
  private double calculateMisplacedLayersHeuristic(String state) {
    String[] stateParts = state.split(";");
    int misplacedLayers = 0;

    // Start checking bottles from the third part of the state (index 2)
    for (int i = 2; i < stateParts.length; i++) {
      String bottle = stateParts[i];

      if (bottle.isEmpty()) {
        continue; // Empty bottles don't have misplaced layers
      }

      // Check for misplaced layers in the bottle
      char firstColor = bottle.charAt(0);
      for (int j = 1; j < bottle.length(); j++) {
        if (bottle.charAt(j) != firstColor && bottle.charAt(j) != 'e') {
          misplacedLayers++; // Layer is misplaced if it's not the same as the top layer
        }
      }
    }

    return misplacedLayers;
  }

  // Second Heuristic: Number of Non-Homogeneous Bottles
  private double calculateNonHomogeneousHeuristic(String state) {
    String[] stateParts = state.split(";");
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

    return nonHomogeneousCount;
  }

  @Override
  public boolean goalTest(String state) {
    // Parse the current state to get the information about bottles and their layers
    String[] stateParts = state.split(";");
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
        if (bottle.charAt(j) != firstColor && bottle.charAt(j) != ',') {
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