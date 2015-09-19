package one.koslowski.world.api.event;

import one.koslowski.world.api.World;

public class WorldStoppedEvent extends WorldStateEvent
{
  private static final long serialVersionUID = 1L;
  
  public WorldStoppedEvent(World source)
  {
    super(source);
  }
  
}