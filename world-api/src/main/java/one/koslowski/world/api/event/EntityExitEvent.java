package one.koslowski.world.api.event;

import one.koslowski.world.api.Entity;
import one.koslowski.world.api.EntityEvent;

/**
 * Ein Entity ist dabei die Welt zu verlassen.
 */
public class EntityExitEvent extends EntityEvent
{
  private static final long serialVersionUID = 1L;
  
  public EntityExitEvent(Entity source)
  {
    super(source);
  }
  
}