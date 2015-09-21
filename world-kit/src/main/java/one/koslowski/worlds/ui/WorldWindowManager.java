package one.koslowski.worlds.ui;

import java.util.EventObject;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.window.WindowManager;
import org.eclipse.swt.widgets.Display;

import one.koslowski.world.api.EventListener;
import one.koslowski.world.api.World;
import one.koslowski.world.api.World.WorldState;
import one.koslowski.world.api.WorldManager;
import one.koslowski.world.api.event.WorldAddedEvent;
import one.koslowski.world.api.event.WorldRemovedEvent;
import one.koslowski.world.api.event.WorldSuspendedEvent;

public class WorldWindowManager extends WindowManager implements EventListener
{
  private WorldManager manager;
  
  private Map<World, WorldController> controllers;
  
  private List<WorldController> pendingClose;
  
  {
    controllers = new LinkedHashMap<>();
    pendingClose = new LinkedList<>();
  }
  
  public WorldWindowManager(WorldManager manager)
  {
    this.manager = manager;
    this.manager.addListener(this);
  }
  
  @Override
  public void processEvent(EventObject event)
  {
    if (event instanceof WorldAddedEvent)
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
    else if (event instanceof WorldSuspendedEvent)
    {
      Display.getDefault().asyncExec(() ->
      {
        WorldController controller = controllers.get(event.getSource());
        
        if (pendingClose.remove(controller))
          removeController(controllers.get(event.getSource()));
      });
    }
    else if (event instanceof WorldRemovedEvent)
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
    manager.execute(world);
  }
  
  public void removeController(WorldController controller)
  {
    World world = controller.getWorld();
    
    if (world.getState() == WorldState.EXECUTING || world.getState().isWaiting())
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
    for (WorldController controller : getControllers())
    {
      removeController(controller);
    }
    
    return super.close();
  }
}