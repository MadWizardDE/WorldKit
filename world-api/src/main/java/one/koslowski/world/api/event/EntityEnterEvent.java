package one.koslowski.world.api.event;

import one.koslowski.world.api.Entity;
import one.koslowski.world.api.EntityEvent;

/**
 * Ein Entity ist dabei die Welt zu betreten.
 */
public class EntityEnterEvent extends EntityEvent
{
  private static final long serialVersionUID = 1L;
  
  public EntityEnterEvent(Entity source)
  {
    super(source);
  }
}