package one.koslowski.world.api;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import one.koslowski.world.api.event.EntityEnterEvent;
import one.koslowski.world.api.event.EntityExitEvent;
import one.koslowski.world.api.event.strategy.BroadcastStrategy;
import one.koslowski.world.api.exception.EntityInvocationException;

public class EntityManager implements EventListener
{
  World world;
  
  EntityEventStrategy strategy;
  
  EntityInvocationHandler invocationHandler;
  
  private Map<Entity, Void> entities;
  
  long entityID = 0;
  
  {
    entities = new WeakHashMap<>();
  }
  
  EntityManager(World world)
  {
    (this.world = world).addContext(new EntityContext());
    
    // Default Strategy
    strategy = new BroadcastStrategy();
    
    // Default InvocationHandler
    invocationHandler = new DefaultInvocationHandler();
  }
  
  public EntityEventStrategy getEventStrategy()
  {
    return strategy;
  }
  
  public EntityInvocationHandler getInvocationHandler()
  {
    return invocationHandler;
  }
  
  public void setEventStrategy(EntityEventStrategy strategy)
  {
    if (strategy == null)
      throw new IllegalArgumentException();
      
    this.strategy = strategy;
  }
  
  public void setInvocationHandler(EntityInvocationHandler invocationHandler)
  {
    if (invocationHandler == null)
      throw new IllegalArgumentException();
      
    this.invocationHandler = invocationHandler;
  }
  
  Set<Entity> getEntities()
  {
    return entities.keySet();
  }
  
  void register(Entity entity)
  {
    entity.id = ++entityID;
    entity.ctx = entity.new EntityContext(this);
    entity.publishEvent(new EntityEnterEvent(entity));
    
    entities.put(entity, null);
  }
  
  Object invoke(EntityInvocation invocation) throws Throwable
  {
    try
    {
      return invocationHandler.dispatch(invocation);
    }
    catch (Error | RuntimeException e)
    {
      throw new EntityInvocationException(invocation, e);
    }
  }
  
  void unregister(Entity entity)
  {
    entity.publishEvent(new EntityExitEvent(entity));
    entity.ctx = null;
    
    entities.remove(entity);
  }
  
  @Override
  public void processEvent(EventObject event)
  {
    if (event instanceof WorldEvent)
      strategy.processEvent((WorldEvent) event);
  }
  
  public class EntityContext implements Context
  {
    EntityContext()
    {
    
    }
    
    public World getWorld()
    {
      return world;
    }
    
    public EntityManager getManager()
    {
      return EntityManager.this;
    }
    
    public int count(Class<?> type)
    {
      int count = 0;
      for (Entity entity : getManager().getEntities())
        if (type.isInstance(entity))
          count++;
          
      return count;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Entity> T getEntity(Class<T> type)
    {
      for (Entity entity : getManager().getEntities())
        if (type.isInstance(entity))
          return (T) entity;
          
      return null;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Entity> T getEntityByID(long id)
    {
      for (Entity entity : getManager().getEntities())
        if (entity.id == id)
          return (T) entity;
          
      return null;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Entity> List<T> getEntities(Class<T> type)
    {
      List<T> result = new ArrayList<>();
      
      for (Entity entity : getManager().getEntities())
        if (type.isInstance(entity))
          result.add((T) entity);
          
      return result;
    }
    
    public List<Entity> getEverything()
    {
      return new ArrayList<>(getManager().getEntities());
    }
  }
  
  private class DefaultInvocationHandler extends EntityInvocationHandler
  {
    @Override
    protected Object dispatch(EntityInvocation invocation) throws Throwable
    {
      throw new InterruptedException();
    }
  }
}