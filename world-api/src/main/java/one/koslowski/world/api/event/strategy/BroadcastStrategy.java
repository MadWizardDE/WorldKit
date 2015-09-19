package one.koslowski.world.api.event.strategy;

import one.koslowski.world.api.Entity;
import one.koslowski.world.api.EntityEventStrategy;
import one.koslowski.world.api.World;
import one.koslowski.world.api.WorldEvent;

public class BroadcastStrategy extends EntityEventStrategy
{
  @Override
  public void processEvent(WorldEvent event)
  {
    for (Entity entity : World.getEntityContext().getEverything())
    {
      handle(entity, event);
    }
  }
}