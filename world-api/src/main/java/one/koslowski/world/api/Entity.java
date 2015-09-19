package one.koslowski.world.api;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;

import one.koslowski.world.api.event.EntityExceptionEvent;
import one.koslowski.world.api.event.EntityInvocationEvent;
import one.koslowski.world.api.event.strategy.EventSourceStrategy;
import one.koslowski.world.api.exception.EntityEventException;

public class Entity implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  private static ThreadLocal<Entity> ENTITY = new ThreadLocal<>();
  
  transient EntityContext ctx;
  transient EventBus      bus;
  
  /** Dynamisches Verhalten (u.a. für Strategie-Implementierung) */
  public transient final Object x;
  
  long id;
  
  private Map<String, Serializable> data;
  
  {
    data = new HashMap<String, Serializable>();
  }
  
  protected Entity()
  {
    bus = new EventBus(this::handleEventException);
    
    x = createProxy();
    
    World.getWorld().addEntity(this);
  }
  
  private Object createProxy()
  {
    List<Class<?>> list = new ArrayList<Class<?>>();
    
    // Strategie-Interfaces
    for (Class<?> cls : getClass().getClasses())
      if (cls != EntityInvocationStrategy.class && cls.isInterface())
        if (EntityInvocationStrategy.class.isAssignableFrom(cls))
          list.add(cls);
          
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    
    return Proxy.newProxyInstance(cl, list.toArray(new Class[list.size()]), this::handleInvocation);
  }
  
  static Entity getEntity()
  {
    return ENTITY.get();
  }
  
  public long getId()
  {
    return id;
  }
  
  public Serializable getData()
  {
    return this.getData(null);
  }
  
  public Serializable getData(String key)
  {
    return this.data.get(key);
  }
  
  public World getWorld()
  {
    return ctx != null ? ctx.getWorld() : null;
  }
  
  public void setData(Serializable data)
  {
    this.setData(null, data);
  }
  
  public void setData(String key, Serializable data)
  {
    this.data.put(key, data);
  }
  
  protected void handleEvent(WorldEvent event) throws Throwable
  {
    ENTITY.set(this);
    
    try
    {
      bus.post(event);
    }
    finally
    {
      ENTITY.remove();
    }
  }
  
  private void handleEventException(Throwable t, SubscriberExceptionContext ctx)
  {
    publishEvent(new EntityExceptionEvent(new EntityEventException(this, (WorldEvent) ctx.getEvent(), t)));
  }
  
  private Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable
  {
    ENTITY.set(Entity.this);
    
    try
    {
      EntityInvocation invocation = new EntityInvocation(Entity.this, method, args);
      
      try
      {
        return invocation.result = ctx.getManager().invoke(invocation);
      }
      catch (Throwable t)
      {
        throw invocation.exception = t;
      }
      finally
      {
        publishEvent(new EntityInvocationEvent(invocation));
      }
    }
    finally
    {
      ENTITY.remove();
    }
  }
  
  private Object writeReplace() throws ObjectStreamException
  {
    return new EntityReference(this);
  }
  
  protected final EntityContext getContext()
  {
    if (ctx == null)
      throw new IllegalStateException();
      
    return ctx;
  }
  
  protected final void publishEvent(EntityEvent event)
  {
    if (event.getSource() != this)
      throw new IllegalArgumentException();
      
    ctx.getWorld().publishEvent(event);
  }
  
  @Override
  protected final void finalize() throws Throwable
  {
    if (ctx != null)
    {
      // TODO EntitySwepper
      // WorldManager.sync(ctx.getWorld(), new EntitySweeperTask());
    }
  }
  
  /**
   * TODO Metamodel Cache
   * 
   * TODO verschieben
   */
  @Deprecated
  class MetaModel
  {
    <T extends Annotation> List<Method> getMethods(Class<T> annotation)
    {
      List<Method> methods = new ArrayList<>();
      
      for (Method method : Entity.this.getClass().getMethods())
      {
        T handler = method.getAnnotation(annotation);
        
        if (handler != null)
        {
          methods.add(method);
        }
      }
      
      return methods;
    }
    
    List<Method> getMethods(Class<?> type, Class<?>... types)
    {
      List<Method> methods = new ArrayList<>();
      
      for (Method method : getMethods())
      {
        if (matches(method, type, types))
          methods.add(method);
      }
      
      return methods;
    }
    
    boolean matches(Method method, Class<?> type, Class<?>... types)
    {
      if (method.getReturnType() != type)
        return false;
        
      if (method.getParameterTypes().length != types.length)
        return false;
        
      for (int i = 0; i < types.length; i++)
        if (method.getParameterTypes()[i] != types[i])
          return false;
          
      // nur Error & RuntimeException zulassen
      for (Class<?> ex : method.getExceptionTypes())
      {
        if (Error.class.isAssignableFrom(ex))
          continue;
        if (RuntimeException.class.isAssignableFrom(ex))
          continue;
        return false;
      }
      
      return true;
    }
    
    boolean matches(Method method, Class<? extends Annotation> annotation)
    {
      return method.getAnnotation(annotation) != null;
    }
    
    Object invoke(Method method, Object obj, Object... args)
    {
      try
      {
        method.setAccessible(true);
        
        return method.invoke(obj, args);
      }
      catch (InvocationTargetException e)
      {
        if (e.getCause() instanceof Error)
          throw (Error) e.getCause();
        if (e.getCause() instanceof RuntimeException)
          throw (RuntimeException) e.getCause();
        return null; // impossibruuuuuu
      }
      catch (IllegalAccessException e)
      {
        throw new RuntimeException(e);
      }
    }
    
    private Set<Method> getMethods()
    {
      Set<Method> methods = new HashSet<>();
      
      Class<?> type = Entity.this.getClass();
      
      while (type != null)
      {
        methods.addAll(Arrays.asList(type.getDeclaredMethods()));
        
        type = type.getSuperclass();
      }
      
      return methods;
    }
  }
  
  public class EntityContext extends EntityManager.EntityContext
  {
    EntityContext(EntityManager manager)
    {
      manager.super();
    }
    
    @SuppressWarnings("unchecked")
    private <T> T getStrategy(Class<T> strg)
    {
      if (!strg.isInstance(getManager().strategy))
        throw new IllegalStateException();
        
      return (T) getManager().strategy;
    }
    
    public void addListener(Class<? extends WorldEvent> type, Object source)
    {
      getStrategy(EventSourceStrategy.class).addListener(type, source, Entity.this);
    }
    
    public void removeListener(Class<? extends WorldEvent> type, Object source)
    {
      getStrategy(EventSourceStrategy.class).removeListener(type, source, Entity.this);
    }
  }
  
  private static class EntityReference
  {
    private long id;
    
    private EntityReference(Entity entity)
    {
      this.id = entity.id;
    }
    
    private Object readResolve() throws ObjectStreamException
    {
      return World.getEntityContext().getEntityByID(id);
    }
  }
  
  @SuppressWarnings("unused")
  private class EntitySweeperTask implements Runnable
  {
    @Override
    public void run()
    {
      // LOG.warn("Entity leaked: " + entity);
      
      ctx.getManager().unregister(Entity.this);
    }
  }

  @SuppressWarnings("unchecked")
  public interface EntityInvocationStrategy<E extends Entity>
  {
    default E getEntity()
    {
      return (E) ENTITY.get();
    }
    
    default EntityContext getContext()
    {
      return getEntity().getContext();
    }
  }
}