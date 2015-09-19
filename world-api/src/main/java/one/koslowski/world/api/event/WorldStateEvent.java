package one.koslowski.world.api.event;

import one.koslowski.world.api.SystemEvent;
import one.koslowski.world.api.World;
import one.koslowski.world.api.World.WorldState;

public class WorldStateEvent extends SystemEvent
{
  private static final long serialVersionUID = 1L;
  
  public WorldStateEvent(World source)
  {
    super(source);
  }
  
  @Override
  public World getSource()
  {
    return (World) super.getSource();
  }
  
  public WorldState getState()
  {
    return getSource().getState();
  }
  
}