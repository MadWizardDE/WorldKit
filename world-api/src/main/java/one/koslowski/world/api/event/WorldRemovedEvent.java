package one.koslowski.world.api.event;

import one.koslowski.world.api.World;

public class WorldRemovedEvent extends WorldManagementEvent
{
  private static final long serialVersionUID = 1L;

  public WorldRemovedEvent(World source)
  {
    super(source);
  }

}