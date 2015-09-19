package one.koslowski.connect4.api.events;

import one.koslowski.connect4.api.Connect4Player;
import one.koslowski.world.api.EntityEvent;

public class MoveEvent extends EntityEvent
{
  private static final long serialVersionUID = 1L;
  
  private int     x;
  private Integer y;
  
  private Boolean legal;
  
  public MoveEvent(Connect4Player source, int x, Integer y, Boolean legal)
  {
    super(source);
    
    this.x = x;
    this.legal = legal;
  }
  
  public Connect4Player getPlayer()
  {
    return (Connect4Player) source;
  }
  
  public int getX()
  {
    return x;
  }
  
  public Integer getY()
  {
    return y;
  }
  
  public boolean isUndo()
  {
    return legal == null;
  }
  
  public boolean isIllegal()
  {
    return !isUndo() && !legal;
  }
  
}