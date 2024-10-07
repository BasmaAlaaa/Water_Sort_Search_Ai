package code;
import java.util.Stack;

public class Bottle {

  private Stack<String> layers; // Stack to hold the layers of liquid
  private int capacity; // Maximum capacity of the bottle

  // Constructor
  public Bottle(int capacity) {
    this.capacity = capacity;
    this.layers = new Stack<>();
  }

  // Check if the bottle is empty
  public boolean isEmpty() {
    return layers.isEmpty();
  }

  // Check if the bottle is full
  public boolean isFull() {
    return layers.size() == capacity;
  }

  // Get the topmost layer color
  public String topLayer() {
    return isEmpty() ? null : layers.peek();
  }

  // Add a layer of liquid to the bottle
  public void addLayer(String color) {
    if (!isFull()) {
      layers.push(color);
    } else {
      throw new IllegalStateException("Bottle is full");
    }
  }

  // Remove and return the top layer of liquid
  public String removeLayer() {
    if (!isEmpty()) {
      return layers.pop();
    } else {
      throw new IllegalStateException("Bottle is empty");
    }
  }

  // Check how many consecutive layers of the same color are on top
  public int countTopSameColorLayers() {
    if (isEmpty())
      return 0;

    String topColor = topLayer();
    int count = 0;
    for (int i = layers.size() - 1; i >= 0; i--) {
      if (layers.get(i).equals(topColor)) {
        count++;
      } else {
        break;
      }
    }
    return count;
  }

  // Check how many empty spaces are left in the bottle
  public int emptySpaces() {
    return capacity - layers.size();
  }

  // Pour layers from this bottle to another bottle
  public void pourInto(Bottle target) {
    if (target.isFull()) {
      throw new IllegalStateException("Target bottle is full");
    }

    // Check if pouring is allowed (target bottle is either empty or has the same
    // color on top)
    if (!target.isEmpty() && !this.topLayer().equals(target.topLayer())) {
      throw new IllegalStateException("Cannot pour into a bottle with a different top layer color");
    }

    int pourableLayers = Math.min(this.countTopSameColorLayers(), target.emptySpaces());
    for (int i = 0; i < pourableLayers; i++) {
      target.addLayer(this.removeLayer());
    }
  }

  // Get the current state of the bottle
  public Stack<String> getLayers() {
    return (Stack<String>) layers.clone(); // Return a copy to avoid external modification
  }

  // Print the bottle contents (for debugging)
  public void printBottle() {
    System.out.println("Bottle: " + layers.toString());
  }
}