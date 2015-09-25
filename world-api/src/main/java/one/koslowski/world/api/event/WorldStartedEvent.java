package one.koslowski.world.api.event;

import one.koslowski.world.api.World;

public class WorldStartedEvent extends WorldStateEvent
{
  private static final long serialVersionUID = 1L;

  public WorldStartedEvent(World source)
  {
    super(source);
  }

}