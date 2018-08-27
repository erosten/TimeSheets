
package cps.excel.TimeSheet;

public class IntPair {

  // Ideally, name the class after whatever you're actually using
  // the int pairs *for.*
  final int x;
  final int y;

  public IntPair(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }
  // depending on your use case, equals? hashCode? More methods?
}
