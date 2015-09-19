package one.koslowski.world.api;

import one.koslowski.world.api.event.EntityExceptionEvent;
import one.koslowski.world.api.exception.EntityEventException;

public abstract class EntityEventStrategy
{
  protected abstract void processEvent(WorldEvent event);
  
  protected final void handle(Entity entity, WorldEvent event)
  {
    try
    {
      entity.handleEvent(event);
    }
    catch (Throwable e)
    {
      entity.publishEvent(new EntityExceptionEvent(new EntityEventException(entity, event, e)));
    }
  }
}