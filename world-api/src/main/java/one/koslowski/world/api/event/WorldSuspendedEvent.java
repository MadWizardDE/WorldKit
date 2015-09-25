package one.koslowski.world.api.event;

import one.koslowski.world.api.World;

public class WorldSuspendedEvent extends WorldStateEvent
{
  private static final long serialVersionUID = 1L;

  public WorldSuspendedEvent(World source)
  {
    super(source);
  }

}