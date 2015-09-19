package one.koslowski.world.api.event.strategy;

import one.koslowski.world.api.Entity;
import one.koslowski.world.api.WorldEvent;

public interface EventSourceStrategy
{
  public void addListener(Class<? extends WorldEvent> type, Object source, Entity entity);
  
  public void removeListener(Class<? extends WorldEvent> type, Object source, Entity entity);
}