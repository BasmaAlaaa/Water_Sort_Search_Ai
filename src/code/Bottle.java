package code;

import java.util.Stack;

public class Bottle {

  private Stack<String> layers;
  private int capacity;

  public Bottle(int capacity) {
    this.capacity = capacity;
    this.layers = new Stack<>();
  }

  public boolean isEmpty() {
    return layers.isEmpty();
  }

  public boolean isFull() {
    return layers.size() == capacity;
  }

  public String topLayer() {
    return isEmpty() ? null : layers.peek();
  }

  public void addLayer(String color) {
    if (!isFull()) {
      layers.push(color);
    } else {
      throw new IllegalStateException("Bottle is full");
    }
  }

  public String removeLayer() {
    if (!isEmpty()) {
      return layers.pop();
    } else {
      throw new IllegalStateException("Bottle is empty");
    }
  }

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

  public int emptySpaces() {
    return capacity - layers.size();
  }

  public int getCapacity() {
    return capacity;
  }

  public int pourInto(Bottle target) {
    if (target.isFull()) {
      throw new IllegalStateException("Target bottle is full");
    }

    if (!target.isEmpty() && !this.topLayer().equals(target.topLayer())) {
      throw new IllegalStateException("Cannot pour into a bottle with a different top layer color");
    }

    int pourableLayers = Math.min(this.countTopSameColorLayers(), target.emptySpaces());
    for (int i = 0; i < pourableLayers; i++) {
      target.addLayer(this.removeLayer());
    }
    return pourableLayers;

  }

  public Stack<String> getLayers() {
    return (Stack<String>) layers.clone();
  }

  public void printBottle() {
    System.out.println("Bottle: " + layers.toString());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;

    Bottle bottle = (Bottle) obj;
    return this.getLayers().equals(bottle.getLayers());
  }

  @Override
  public int hashCode() {
    return getLayers().hashCode();
  }

}