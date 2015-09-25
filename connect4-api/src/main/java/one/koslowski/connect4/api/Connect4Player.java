package one.koslowski.connect4.api;

import one.koslowski.world.api.Entity;

public class Connect4Player extends Entity
{
  private static final long serialVersionUID = 1L;

  private String name;

  private Color color;

  public Connect4Player(String name, Color color)
  {
    if (color == null)
      throw new IllegalArgumentException("color == null");

    this.name = name;

    this.color = color;
  }

  public String getName()
  {
    return name;
  }

  public Color getColor()
  {
    return color;
  }

  public interface Connect4PlayerStrategy extends EntityInvocationStrategy<Connect4Player>
  {
    int move() throws UndoMoveException, InterruptedException;
  }

  public enum Color
  {
    RED, BLUE, GREEN, YELLOW;
  }
}