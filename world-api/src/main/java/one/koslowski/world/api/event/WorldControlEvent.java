package one.koslowski.world.api.event;

import one.koslowski.world.api.SystemEvent;
import one.koslowski.world.api.World;

public class WorldControlEvent extends SystemEvent
{
  private static final long serialVersionUID = 1L;
  
  public WorldControlEvent(World source)
  {
    super(source);
  }
  
  @Override
  public World getSource()
  {
    return (World) super.getSource();
  }
  
}