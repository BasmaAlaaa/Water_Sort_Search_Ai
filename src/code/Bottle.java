package code;

import java.util.Stack;

public class Bottle {
  private Stack<Character> layers; // Stack to represent layers in the bottle
  private int capacity; // Capacity of the bottle

  // Constructor
  public Bottle(int capacity) {
    this.capacity = capacity;
    this.layers = new Stack<>();
  }

  // Add a layer to the bottle
  public void addLayer(char color) {
    if (layers.size() < capacity && color != 'e') {
      layers.push(color);
    }
  }

  // Remove the top layer from the bottle
  public char removeLayer() {
    if (!layers.isEmpty()) {
      return layers.pop();
    }
    return 'e'; // Return 'e' if the bottle is empty
  }

  // Get the top color without removing it
  public char getTopColor() {
    if (!layers.isEmpty()) {
      return layers.peek();
    }
    return 'e'; // Return 'e' if the bottle is empty
  }

  // Check if the bottle is empty
  public boolean isEmpty() {
    return layers.isEmpty();
  }

  // Check if the bottle is full
  public boolean isFull() {
    return layers.size() == capacity;
  }

  // Get the number of layers in the bottle
  public int size() {
    return layers.size();
  }

  // Return the capacity of the bottle
  public int getCapacity() {
    return capacity;
  }

  // Return a deep copy of the bottle
  public Bottle deepCopy() {
    Bottle copy = new Bottle(this.capacity);
    copy.layers.addAll(this.layers);
    return copy;
  }

  public Stack<Character> getLayers() {
    return layers;
  }

  @Override
  public String toString() {
    return layers.toString();
  }
}
