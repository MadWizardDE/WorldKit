package one.koslowski.worlds.ui;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.window.WindowManager;
import org.eclipse.swt.widgets.Display;

import one.koslowski.world.api.World;
import one.koslowski.world.api.World.WorldState;
import one.koslowski.world.api.WorldEvent;
import one.koslowski.world.api.WorldEventListener;
import one.koslowski.world.api.WorldManager;
import one.koslowski.world.api.event.WorldAddedEvent;
import one.koslowski.world.api.event.WorldRemovedEvent;
import one.koslowski.world.api.event.WorldSuspendedEvent;

public class WorldWindowManager extends WindowManager implements WorldEventListener
{
  private WorldManager manager;
  
  private Map<World, WorldController> controllers;
  
  private Set<WorldController> pendingClose;
  
  {
    controllers = new LinkedHashMap<>();
    pendingClose = new LinkedHashSet<>();
  }
  
  public WorldWindowManager(WorldManager manager)
  {
    this.manager = manager;
    this.manager.addListener(this);
  }
  
  @Override
  public void processEvent(WorldEvent event)
  {
    if (event instanceof WorldAddedEvent)
      onAdd((WorldAddedEvent) event);
    else if (event instanceof WorldSuspendedEvent)
      onSuspend((WorldSuspendedEvent) event);
    else if (event instanceof WorldRemovedEvent)
      onRemove((WorldRemovedEvent) event);
  }
  
  private void onAdd(WorldAddedEvent event)
  {
    Display.getDefault().asyncExec(() ->
    {
      WorldController controller = controllers.get(event.getSource());
      
      WorldWindow window = getWindow(controller);
      
      if (window == null)
      {
        window = getWindow(null);
        
        if (window == null)
        {
          add(window = new WorldWindow());
        }
      }
      
      window.setController(controller);
    });
  }
  
  private void onSuspend(WorldSuspendedEvent event)
  {
    Display.getDefault().asyncExec(() ->
    {
      WorldController controller = controllers.get(event.getSource());
      
      if (pendingClose.remove(controller))
        removeController(controllers.get(event.getSource()));
    });
  }
  
  private void onRemove(WorldRemovedEvent event)
  {
    Display.getDefault().asyncExec(() ->
    {
      WorldController controller = controllers.remove(event.getSource());
      
      WorldWindow window = getWindow(controller);
      
      if (window != null)
      {
        window.setController(null);
        
        if (getWindowCount() > 1)
          window.close();
        else
        {
          // den nächstbesten Controller anzeigen
          for (WorldController c : controllers.values())
          {
            window.setController(c);
            break;
          }
        }
      }
    });
  }
  
  public WorldController[] getControllers()
  {
    WorldController bs[] = new WorldController[controllers.size()];
    controllers.values().toArray(bs);
    return bs;
  }
  
  public void addController(WorldController controller)
  {
    World world = controller.getWorld();
    
    controllers.put(world, controller);
    
    // hinzufügen & starten
    manager.addWorld(world);
    if (world.getState() == WorldState.FRESH)
      manager.execute(world);
  }
  
  public void removeController(WorldController controller)
  {
    World world = controller.getWorld();
    
    if (world.getState().isRunning())
    {
      pendingClose.add(controller);
      
      controller.getWorld().interrupt();
    }
    else
      manager.removeWorld(world);
  }
  
  public WorldWindow getWindow(WorldController controller)
  {
    for (Window window : getWindows())
      if (window instanceof WorldWindow)
        if (((WorldWindow) window).controller == controller)
          return (WorldWindow) window;
          
    // Single Window-Mode
    if (getWindowCount() > 0)
      return (WorldWindow) getWindows()[0];
      
    return null;
  }
  
  @Override
  public void add(Window window)
  {
    if (!(window instanceof WorldWindow))
      throw new IllegalArgumentException();
      
    super.add(window);
    
    if (window.getShell() == null)
      window.open();
  }
  
  public void focus(World world)
  {
    WorldWindow window = getWindow(controllers.get(world));
    
    if (window != null)
    {
      window.getShell().setFocus();
    }
  }
  
  @Override
  public boolean close()
  {
    boolean closedAll = super.close();
    
    // for (WorldController controller : getControllers())
    // {
    // removeController(controller);
    // }
    
    return closedAll;
  }
}