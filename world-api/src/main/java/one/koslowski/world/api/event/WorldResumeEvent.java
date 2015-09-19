package one.koslowski.world.api.event;

import one.koslowski.world.api.World;

public class WorldResumeEvent extends WorldStateEvent
{
  private static final long serialVersionUID = 1L;
  
  public WorldResumeEvent(World source)
  {
    super(source);
  }
  
}