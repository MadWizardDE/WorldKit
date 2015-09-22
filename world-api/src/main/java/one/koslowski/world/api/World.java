package one.koslowski.world.api;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import one.koslowski.world.api.EntityManager.EntityContext;
import one.koslowski.world.api.event.ExceptionEvent;
import one.koslowski.world.api.event.WorldResumedEvent;
import one.koslowski.world.api.event.WorldStartedEvent;
import one.koslowski.world.api.event.WorldSuspendedEvent;

public abstract class World implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  // +++ Manager +++ //
  
  transient WorldManager manager;
  
  transient EntityManager entityManager;
  
  transient WorldTask<?> task;
  
  // +++ Steuerung +++ //
  
  private transient Map<Class<?>, Context> contexts;
  private transient List<EventListener>    listeners;
  private transient ExceptionHandler       exceptionHandler;
  
  // +++ Welt-Zustand +++ //
  
  long frame;
  
  volatile WorldState state;
  
  private FrameDelimiter frameDelimiter;
  
  protected Phase phase = null;
  
  volatile Entity wait;
  
  {
    listeners = new CopyOnWriteArrayList<>();
    
    contexts = new ConcurrentHashMap<>();
  }
  
  protected World()
  {
    state = WorldState.FRESH;
    
    // Entity-Manager einrichten
    addListener(entityManager = new EntityManager(this));
    
    // Default ExceptionHandler
    exceptionHandler = new DefaultExceptionHandler();
  }
  
  static World getWorld()
  {
    WorldTask<?> task = WorldManager.getTask();
    
    if (task == null)
      throw new IllegalStateException();
      
    return task.getWorld();
  }
  
  protected void addContext(Context ctx)
  {
    contexts.put(ctx.getClass(), ctx);
  }
  
  protected void removeContext(Context ctx)
  {
    if (ctx instanceof EntityContext)
      throw new IllegalArgumentException();
      
    contexts.remove(ctx.getClass());
  }
  
  @SuppressWarnings("unchecked")
  protected static <T extends WorldContext<?>> T getContext(Class<T> ctx)
  {
    return (T) getWorld().contexts.get(ctx);
  }
  
  public static EntityContext getEntityContext()
  {
    return getContext(EntityContext.class);
  }
  
  public WorldState getState()
  {
    return state;
  }
  
  public WorldManager getManager()
  {
    return manager;
  }
  
  public EntityManager getEntityManager()
  {
    return entityManager;
  }
  
  public void addListener(EventListener l)
  {
    listeners.add(l);
  }
  
  public void removeListener(EventListener l)
  {
    listeners.remove(l);
  }
  
  public boolean isWaiting(Object object)
  {
    return wait == object;
  }
  
  public void notify(Object object)
  {
    if (object == null)
      throw new IllegalArgumentException();
      
    if (isWaiting(object))
    {
      wait = null;
      
      manager.execute(this);
    }
  }
  
  public void interrupt()
  {
    switch (state)
    {
      case EXECUTING:
        if (task != null)
          task.interrupt();
        break;
        
      case WAITING:
        wait = null;
      case THROTTLING:
        state = WorldState.INTERRUPTED;
        
        publishEvent(new WorldSuspendedEvent(this));
        break;
        
      default:
    }
  }
  
  public FrameDelimiter getFrameDelimiter()
  {
    return frameDelimiter;
  }
  
  public ExceptionHandler getExceptionHandler()
  {
    return exceptionHandler;
  }
  
  public void setFrameDelimiter(FrameDelimiter delimiter)
  {
    this.frameDelimiter = delimiter;
  }
  
  public void setExceptionHandler(ExceptionHandler handler)
  {
    this.exceptionHandler = handler;
  }
  
  protected void addEntity(Entity entity)
  {
    entityManager.register(entity);
  }
  
  protected void removeEntity(Entity entity)
  {
    entityManager.unregister(entity);
  }
  
  protected final void publishEvent(WorldEvent event)
  {
    event.world = this;
    
    publishEvent((EventObject) event);
  }
  
  final void publishEvent(EventObject event)
  {
    if (event instanceof ExceptionEvent)
    {
      exceptionHandler.trap((ExceptionEvent) event);
    }
    else
    {
      for (EventListener l : listeners)
      {
        l.processEvent(event);
      }
    }
  }
  
  /**
   * Die Haupt-Schleife - hier beginnt und endet alles.
   */
  final void loop()
  {
    boolean first = state == WorldState.FRESH;
    
    if (phase == null)
      return;
      
    state = WorldState.EXECUTING;
    
    if (first)
      publishEvent(new WorldStartedEvent(this));
    else
      publishEvent(new WorldResumedEvent(this));
      
    try
    {
      if (frameDelimiter != null)
        frameDelimiter.beforePhase();
        
      phase = phase.get(); // Phase durchführen
      
      frame++;
      
      if (frameDelimiter != null)
        frameDelimiter.afterPhase();
    }
    catch (InterruptedException e)
    {
      if (e instanceof WaitException)
      {
        wait = ((WaitException) e).entity;
      }
      
      Thread.currentThread().interrupt();
    }
    catch (Error | RuntimeException e)
    {
      throw e;
    }
    finally
    {
      if (phase != null)
      {
        manager.queue(new WorldTask<Void>(this, this::loop)); // nächste Ausführung
      }
    }
  }
  
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
  {
    task = WorldManager.getTask();
    task.world = this;
    
    in.defaultReadObject();
    
    entityManager.entityID = in.readLong();
  }
  
  private void writeObject(ObjectOutputStream out) throws IOException
  {
    out.defaultWriteObject();
    
    out.writeLong(entityManager.entityID);
  }
  
  /**
   * Spaltet eine asynchrone Fork-Task von der Hauptverarbeitung ab.
   * 
   * @param fork
   *          Fork-Methode (ohne Rückgabewert)
   * 
   * @return Fork-Task
   */
  protected static Future<?> fork(Runnable fork)
  {
    return fork(Executors.callable(fork));
  }
  
  /**
   * Spaltet eine asynchrone Fork-Task von der Hauptverarbeitung ab.
   * 
   * @param fork
   *          Fork-Methode (mit Rückgabewert)
   * 
   * @return Fork-Task
   */
  protected static <V> Future<V> fork(Callable<V> fork)
  {
    if (WorldManager.getTask() == null)
      throw new IllegalStateException("World unknown");
      
    WorldTask<V> forkTask = new WorldTask<V>(WorldManager.getTask(), fork);
    
    // triviale fork/join logik
    forkTask.world.manager.execute(forkTask);
    
    return forkTask;
  }
  
  /**
   * 
   * @param future
   *          Fork-Task (Rückgabewert von fork())
   * 
   * @return Ergebnis des Fork-Tasks
   * 
   * @throws InterruptedException
   *           unterbrochen, beim Warten auf den Fork-Task
   * @throws RuntimeException
   *           unchecked Exception der Fork-Task
   */
  protected static <V> V join(Future<V> future) throws InterruptedException
  {
    return ((WorldTask<V>) future).await();
  }
  
  /**
   * Wartet auf alle ge-fork()-ten Tasks. Falls Exceptions aufgetreten sind, werden diese ebenfalls
   * aus dieser Methode geworfen, sodass alle Aufrufe gegen Future.get() in O(1) ablaufen und keine
   * zusätzlichen Exceptions mehr werfen.
   * 
   * @throws InterruptedException
   *           unterbrochen, beim Warten auf die Fork-Tasks
   * @throws RuntimeException
   *           unchecked Exception einer Fork-Task
   */
  protected static void sync() throws InterruptedException
  {
    if (WorldManager.getTask() == null)
      throw new IllegalStateException("World unknown");
      
    WorldManager.getTask().awaitTasks();
  }
  
  protected interface Phase extends Serializable
  {
    Phase get() throws InterruptedException;
  }
  
  public enum WorldState
  {
    /** "frische" Welt, bereit zur Ausführung */
    FRESH,
    
    /** Welt wird ausgeführt */
    EXECUTING,
    
    /** Welt wartet auf die verzögerte nächste Ausführung */
    THROTTLING,
    
    /** Welt wartet auf externes Ereignis */
    WAITING,
    
    /** Welt wurde von außen unterbrochen, kann aber fortgesetzt werden */
    INTERRUPTED,
    
    /** Welt wurde aufgrund einer Ausnahme angehalten, kann evtl. wieder gestartet werden */
    EXCEPTION,
    
    /** Welt hat den Endzustand erreicht */
    STOPPED;
    
    public boolean isWaiting()
    {
      return this == THROTTLING || this == WAITING;
    }
    
    public boolean isRunning()
    {
      return isWaiting() || this == WorldState.EXECUTING;
    }
    
    public boolean isSuspended()
    {
      return this == INTERRUPTED || this == EXCEPTION;
    }
  }
  
  protected class WorldContext<T extends World> implements Context
  {
    @SuppressWarnings("unchecked")
    public T getWorld()
    {
      return (T) World.this;
    }
  }
  
  private class DefaultExceptionHandler extends ExceptionHandler
  {
    @Override
    protected boolean handle(ExceptionEvent event)
    {
      event.getException().printStackTrace();
      
      if (state == WorldState.EXECUTING)
      {
        state = WorldState.EXCEPTION;
        
        publishEvent(new WorldSuspendedEvent(World.this));
      }
      
      return true;
    }
  }
}