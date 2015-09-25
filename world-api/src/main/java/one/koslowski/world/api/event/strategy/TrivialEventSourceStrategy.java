package one.koslowski.world.api.event.strategy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import one.koslowski.world.api.Entity;
import one.koslowski.world.api.EntityEventStrategy;
import one.koslowski.world.api.WorldEvent;

public class TrivialEventSourceStrategy extends EntityEventStrategy implements EventSourceStrategy
{
  private Map<Class<? extends WorldEvent>, Map<Object, Set<Entity>>> listeners;

  {
    listeners = new HashMap<>();
  }

  @Override
  public void processEvent(WorldEvent event)
  {
    // TODO Trivial-Implementierung

    // egal
    for (Entity entity : listeners(null, null))
      handle(entity, event);

    // passender Typ
    for (Entity entity : listeners(event.getClass(), null))
      handle(entity, event);

    // passende Quelle
    for (Entity entity : listeners(null, event.getSource()))
      handle(entity, event);

    // beides passt
    for (Entity entity : listeners(event.getClass(), event.getSource()))
      handle(entity, event);
  }

  @Override
  public void addListener(Class<? extends WorldEvent> type, Object source, Entity entity)
  {
    listeners(type, source).add(entity);
  }

  @Override
  public void removeListener(Class<? extends WorldEvent> type, Object source, Entity entity)
  {
    listeners(type, source).remove(entity);
  }

  Set<Entity> listeners(Class<? extends WorldEvent> type, Object source)
  {
    Map<Object, Set<Entity>> t = this.listeners.get(type);

    if (t == null)
    {
      this.listeners.put(type, t = new WeakHashMap<>());
    }

    Set<Entity> s = t.get(source);

    if (s == null)
    {
      t.put(type, s = new HashSet<>());
    }

    return s;
  }

}