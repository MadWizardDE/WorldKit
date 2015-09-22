package one.koslowski.world.api;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import one.koslowski.world.api.FrameDelimiter.ThrottleException;
import one.koslowski.world.api.World.WorldState;
import one.koslowski.world.api.event.WorldExceptionEvent;
import one.koslowski.world.api.event.WorldSuspendedEvent;
import one.koslowski.world.api.event.WorldWaitEvent;

class WorldTask<V> extends FutureTask<V>
{
  World world;
  
  WorldTask<?> parent;
  
  Collection<WorldTask<?>> tasks;
  
  Long delay;
  
  {
    tasks = new LinkedList<>();
  }
  
  public WorldTask(World world, Runnable task)
  {
    this(world, Executors.callable(task, null));
  }
  
  public WorldTask(World world, Callable<V> task)
  {
    super(task);
    
    this.world = world;
  }
  
  public WorldTask(WorldTask<?> worldTask, Callable<V> task)
  {
    this(worldTask.getWorld(), task);
    
    // Parent->Child Beziehung herstellen
    (this.parent = worldTask).tasks.add(this);
  }
  
  public World getWorld()
  {
    return world;
  }
  
  V await() throws InterruptedException
  {
    try
    {
      awaitTasks();
      
      return this.get();
    }
    catch (CancellationException e)
    {
      throw new InterruptedException();
    }
    catch (ExecutionException e)
    {
      if (e.getCause() instanceof Error)
        throw (Error) e.getCause();
      if (e.getCause() instanceof RuntimeException)
        throw (RuntimeException) e.getCause();
      return null; // impossibruuuuuu
    }
  }
  
  void awaitTasks() throws InterruptedException
  {
    for (WorldTask<?> task : tasks)
      task.await();
  }
  
  void interrupt()
  {
    for (WorldTask<?> task : tasks)
      task.cancel(true);
      
    this.cancel(true);
  }
  
  V invoke() throws InterruptedException
  {
    run();
    
    return await();
  }
  
  @Override
  public void run()
  {
    WorldManager.TASK.set(this);
    
    try
    {
      if (world != null && parent == null)
        synchronized (world)
        {
          world.task = this; // Highlander
          
          try
          {
            super.run();
          }
          finally
          {
            world.task = null;
          }
        }
      else
        super.run();
        
      await(); // FAILSAVE: auf nicht-gejointe Tasks warten
      
      if (Thread.interrupted())
      {
        throw new InterruptedException();
      }
    }
    catch (ThrottleException e)
    {
      world.state = WorldState.THROTTLING;
      
      delay = e.time;
    }
    catch (InterruptedException e)
    {
      if (parent == null)
      {
        if (world.wait != null)
        {
          WorldWaitEvent event = new WorldWaitEvent(world, world.wait);
          
          world.publishEvent(event);
          
          if (event.wait)
            world.state = WorldState.WAITING;
          else
            world.wait = null;
        }
        else if (world.state == WorldState.EXECUTING)
        {
          world.state = WorldState.INTERRUPTED;
          
          world.publishEvent(new WorldSuspendedEvent(world));
        }
      }
    }
    catch (Error | RuntimeException e)
    {
      world.publishEvent(new WorldExceptionEvent(world, e));
    }
    finally
    {
      WorldManager.TASK.remove();
    }
  }
}