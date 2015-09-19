package one.koslowski.world.api.event;

import one.koslowski.world.api.World;

public class WorldAddedEvent extends WorldControlEvent
{
  private static final long serialVersionUID = 1L;
  
  public WorldAddedEvent(World source)
  {
    super(source);
  }
  
}