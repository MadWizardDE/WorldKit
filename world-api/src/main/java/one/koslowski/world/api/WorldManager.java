package one.koslowski.world.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import one.koslowski.world.api.World.WorldState;
import one.koslowski.world.api.event.WorldAddedEvent;
import one.koslowski.world.api.event.WorldManagementEvent;
import one.koslowski.world.api.event.WorldRemovedEvent;
import one.koslowski.world.api.event.WorldStoppedEvent;
import one.koslowski.world.api.event.WorldSuspendedEvent;

public class WorldManager implements WorldEventListener
{
  static final ThreadLocal<WorldTask<?>> TASK = new ThreadLocal<>();

  private WorldExecuter executor;

  private List<WorldEventListener> listeners;

  private Map<World, Queue<WorldTask<?>>> worlds;

  {
    listeners = new CopyOnWriteArrayList<>();

    worlds = new ConcurrentHashMap<>();
  }

  public WorldManager()
  {
    executor = new WorldExecuter();
  }

  static WorldTask<?> getTask()
  {
    return TASK.get();
  }

  public void addListener(WorldEventListener l)
  {
    listeners.add(l);
  }

  public void removeListener(WorldEventListener l)
  {
    listeners.remove(l);
  }

  @Override
  public void processEvent(WorldEvent event)
  {
    // TODO Event-Debugging
    String eventName = event.getClass().getSimpleName();
    String sourceName = event.getSource().getClass().getSimpleName();
    System.out.println(eventName + " @ " + sourceName);

    if (event instanceof WorldManagementEvent)
    {
      if (event instanceof WorldSuspendedEvent)
      {
        World world = (World) event.getSource();

        executor.cancel(world);
      }

      publishEvent((WorldManagementEvent) event);
    }
  }

  public Set<World> getWorlds()
  {
    return worlds.keySet();
  }

  public void addWorld(World world)
  {
    synchronized (world)
    {
      Queue<WorldTask<?>> tasks = this.worlds.get(world);

      if (tasks != null)
        throw new IllegalArgumentException("World already known");

      world.manager = this;
      world.addListener(this);

      this.worlds.put(world, tasks = new ConcurrentLinkedDeque<>());

      publishEvent(new WorldAddedEvent(world));
    }
  }

  public void removeWorld(World world)
  {
    synchronized (world)
    {
      Queue<WorldTask<?>> tasks = this.worlds.remove(world);

      if (tasks == null)
        throw new IllegalArgumentException("World unknown");

      if (tasks != null)
      {
        world.removeListener(this);
        world.manager = null;

        this.worlds.remove(world);

        publishEvent(new WorldRemovedEvent(world));
      }
    }
  }

  /**
   * Startet die Ausführung der Welt oder setzt diese fort.
   */
  public void execute(World world)
  {
    synchronized (world)
    {
      Queue<WorldTask<?>> tasks = worlds.get(world);

      if (tasks == null)
        throw new IllegalArgumentException("World unknown");

      if (world.getState() == WorldState.EXECUTING)
        throw new IllegalStateException("World is executing");
      if (world.getState() == WorldState.STOPPED)
        throw new IllegalStateException("World has ended");

      if (tasks.isEmpty())
        queue(new WorldTask<Void>(world, world::loop));

      if (world.wait != null)
        throw new IllegalStateException("World is waiting");

      execute(worlds.get(world).poll());
    }
  }

  void execute(WorldTask<?> task)
  {
    executor.execute(task);
  }

  void queue(WorldTask<?> task)
  {
    Queue<WorldTask<?>> tasks = worlds.get(task.getWorld());

    tasks.offer(task);
  }

  void publishEvent(WorldManagementEvent event)
  {
    for (WorldEventListener l : listeners)
    {
      l.processEvent(event);
    }
  }

  /**
   * Führt eine Aufgabe synchron im Kontext der Welt und zum Aufrufer aus.
   * 
   * (im gleichen Thread)
   * 
   * @param world
   *          Welt
   * @param task
   *          Aufgabe
   */
  public static void sync(World world, Runnable task)
  {
    sync(world, Executors.callable(task));
  }

  /**
   * Führt eine Aufgabe synchron im Kontext der Welt und zum Aufrufer aus.
   * 
   * (im gleichen Thread)
   * 
   * @param world
   *          Welt
   * @param task
   *          Aufgabe
   */
  public static <V> V sync(World world, Callable<V> task)
  {
    try
    {
      return new WorldTask<V>(world, task).invoke();
    }
    catch (InterruptedException e)
    {
      return null; // TODO ignorieren?
    }
  }

  public static void write(World world, OutputStream output) throws IOException
  {
    IOException io = sync(world, () ->
    {
      try (ObjectOutputStream stream = new ObjectOutputStream(output))
      {
        world.entityManager.fullSerialize = true;

        stream.writeObject(world);

        return null;
      }
      catch (IOException e)
      {
        return e;
      }
      finally
      {
        world.entityManager.fullSerialize = false;
      }
    });

    if (io != null)
      throw io;
  }

  public static World read(InputStream input) throws ClassNotFoundException, IOException
  {
    Object o = sync(null, () ->
    {
      try (ObjectInputStream stream = new ObjectInputStream(input))
      {
        return (World) stream.readObject();
      }
      catch (IOException e)
      {
        return e;
      }
    });

    if (o instanceof ClassNotFoundException)
      throw (ClassNotFoundException) o;
    if (o instanceof IOException)
      throw (IOException) o;
    return (World) o;
  }

  private class WorldExecuter extends ScheduledThreadPoolExecutor
  {
    private Map<RunnableScheduledFuture<?>, WorldTask<?>> tasks;

    {
      tasks = new LinkedHashMap<>();
    }

    private WorldExecuter()
    {
      super(Runtime.getRuntime().availableProcessors());

      setRemoveOnCancelPolicy(true);
    }

    /**
     * Alle geplanten Tasks zur Welt abbrechen.
     */
    private void cancel(World world)
    {
      for (Entry<RunnableScheduledFuture<?>, WorldTask<?>> entry : tasks.entrySet())
        if (entry.getValue().getWorld() == world)
          entry.getKey().cancel(false);
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable r, RunnableScheduledFuture<V> task)
    {
      // FUCK YOU Doug Lea
      if (r instanceof WorldTask)
        tasks.put(task, (WorldTask<?>) r);

      return super.decorateTask(r, task);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r)
    {
      super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t)
    {
      WorldTask<?> task = tasks.get(r);

      try
      {
        if (task.parent == null)
        {
          World world = task.world;

          Queue<WorldTask<?>> queue = worlds.get(world);

          if (queue != null)
            if (queue.isEmpty())
            {
              world.state = WorldState.STOPPED;

              world.publishEvent(new WorldStoppedEvent(world));
            }

          if (world.state == WorldState.EXECUTING)
          {
            execute(queue.poll());
          }
          else if (world.state == WorldState.THROTTLING)
          {
            schedule(queue.poll(), task.delay, TimeUnit.MILLISECONDS);
          }
        }
      }
      finally
      {
        tasks.remove(r);
      }
    }
  }
}