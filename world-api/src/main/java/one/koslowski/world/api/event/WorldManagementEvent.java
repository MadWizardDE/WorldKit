package one.koslowski.world.api.event;

import one.koslowski.world.api.World;
import one.koslowski.world.api.WorldEvent;

public class WorldManagementEvent extends WorldEvent implements SystemEvent
{
  private static final long serialVersionUID = 1L;

  public WorldManagementEvent(World source)
  {
    super(source);
  }

  @Override
  public World getSource()
  {
    return (World) super.getSource();
  }

}