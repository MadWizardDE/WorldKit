package one.koslowski.world.api.event;

import one.koslowski.world.api.World;

public class WorldStartEvent extends WorldStateEvent
{
  private static final long serialVersionUID = 1L;
  
  public WorldStartEvent(World source)
  {
    super(source);
  }
  
}