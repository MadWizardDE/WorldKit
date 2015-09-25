package one.koslowski.world.api.invocation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import one.koslowski.world.api.Entity;
import one.koslowski.world.api.Entity.EntityInvocationStrategy;
import one.koslowski.world.api.EntityInvocation;
import one.koslowski.world.api.EntityInvocationHandler;

public class StaticInvocationHandler extends EntityInvocationHandler
{
  private Map<Entity, List<EntityInvocationStrategy<?>>> strategies;

  {
    strategies = new HashMap<>();
  }

  public StaticInvocationHandler()
  {

  }

  public <E extends Entity, T extends EntityInvocationStrategy<E>> void register(E entity, T strategy)
  {
    List<EntityInvocationStrategy<?>> strategies = this.strategies.get(entity);

    if (strategies == null)
    {
      this.strategies.put(entity, strategies = new ArrayList<>());
    }

    strategies.add(strategy);
  }

  public <E extends Entity, T extends EntityInvocationStrategy<E>> void unregister(E entity, T strategy)
  {
    List<EntityInvocationStrategy<?>> strategies = this.strategies.get(entity);

    if (!CollectionUtils.isEmpty(strategies))
    {
      strategies.remove(strategy);
    }
  }

  @Override
  protected Object dispatch(EntityInvocation invocation) throws Throwable
  {
    List<EntityInvocationStrategy<?>> strategies = this.strategies.get(invocation.getEntity());

    try
    {
      for (EntityInvocationStrategy<?> strategy : strategies)
      {
        if (invocation.getMethod().getDeclaringClass().isInstance(strategy))
        {
          return invocation.getMethod().invoke(strategy, invocation.getParameters());
        }
      }
    }
    catch (InvocationTargetException e)
    {
      throw e.getTargetException();
    }

    throw new IllegalStateException("entity invocation failed");
  }
}