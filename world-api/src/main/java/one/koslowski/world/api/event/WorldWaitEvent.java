package one.koslowski.world.api.event;

import one.koslowski.world.api.World;

public class WorldWaitEvent extends WorldStateEvent
{
  private static final long serialVersionUID = 1L;
  
  private Object object;
  
  public boolean wait = true;
  
  public WorldWaitEvent(World source, Object object)
  {
    super(source);
    
    this.object = object;
  }
  
  public Object getObject()
  {
    return object;
  }
}