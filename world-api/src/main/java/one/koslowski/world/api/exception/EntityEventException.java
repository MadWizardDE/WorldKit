package one.koslowski.world.api.exception;

import one.koslowski.world.api.Entity;
import one.koslowski.world.api.WorldEvent;

public class EntityEventException extends EntityException
{
  private static final long serialVersionUID = 1L;

  private WorldEvent event;

  public EntityEventException(Entity entity, WorldEvent event, Throwable t)
  {
    super(entity, t);

    this.event = event;
  }

  public WorldEvent getEvent()
  {
    return event;
  }

}