package one.koslowski.connect4.api.events;

import one.koslowski.connect4.api.Connect4World;
import one.koslowski.world.api.WorldEvent;

public class GameStartedEvent extends WorldEvent
{
  private static final long serialVersionUID = 1L;
  
  public GameStartedEvent(Connect4World source)
  {
    super(source);
  }
  
}