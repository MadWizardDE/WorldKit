package one.koslowski.worlds;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import one.koslowski.world.api.WorldManager;
import one.koslowski.worlds.ui.WorldWindow;
import one.koslowski.worlds.ui.WorldWindowManager;
import one.koslowski.worlds.ui.connect4.Connect4Controller;
import one.koslowski.worlds.ui.wizard.WizardController;

/**
 * TODOs:
 *
 * - Dispose beim Schließen (z.B. Shutdown-Prozedur)
 * 
 * - Netzwerk-Funktion + GUI: Server-Browser
 * 
 * - Serialize
 * 
 * - Lokaler Spieler
 * 
 * - KI
 * 
 * - Play/Pause
 * 
 * - Headless Mode
 * 
 * @author Kevin Koslowski
 */
public class WorldKit
{
  public static final UserInterface UI = new UserInterface();
  
  public static void main(String args[])
  {
    WorldManager worldManager = new WorldManager();
    
    Display display = new Display();
    
    WorldWindowManager windowManager = new WorldWindowManager(worldManager);
    windowManager.add(new WorldWindow()); // empty window
    
    try
    {
      // Event-Loop
      while (windowManager.getWindowCount() > 0)
        if (!display.readAndDispatch())
          display.sleep();
      display.dispose();
    }
    catch (Throwable t)
    {
      t.printStackTrace();
    }
    finally
    {
      System.exit(0); // TODO Thread-Pool abbauen
    }
  }
  
  public static class UserInterface implements SharedImages, SharedFonts
  {
    private Map<WorldType, ResourceManager> resources;
    private Map<WorldType, ImageRegistry>   images;
    private Map<WorldType, FontRegistry>    fonts;
    
    public void sync(Control control, Runnable exec)
    {
      if (!control.isDisposed())
      {
        control.getDisplay().syncExec(() ->
        {
          if (!control.isDisposed())
          {
            if (exec != null)
              exec.run();
              
            control.redraw();
          }
        });
      }
    }
    
    public void async(Control control, Runnable exec)
    {
      if (!control.isDisposed())
      {
        control.getDisplay().asyncExec(() ->
        {
          if (!control.isDisposed())
          {
            if (exec != null)
              exec.run();
              
            control.redraw();
          }
        });
      }
    }
    
    public ResourceManager getResources(WorldType type)
    {
      if (resources == null)
        resources = new HashMap<>();
        
      ResourceManager manager = resources.get(type);
      
      if (manager == null)
        resources.put(type, manager = new LocalResourceManager(JFaceResources.getResources()));
        
      return manager;
    }
    
    @Override
    public ImageRegistry getImageRegistry(WorldType type)
    {
      if (images == null)
        images = new HashMap<>();
        
      ImageRegistry registry = images.get(type);
      
      if (registry == null)
      {
        images.put(type, registry = new ImageRegistry(getResources(type)));
        
        if (type == null)
          loadImages(registry);
      }
      
      return registry;
    }
    
    private void loadImages(ImageRegistry registry)
    {
      registry.put(IMG_WORLD, ImageDescriptor.createFromFile(WorldWindow.class, "world.png"));
      
      registry.put(IMG_RESUME, ImageDescriptor.createFromFile(WorldWindow.class, "resume.gif"));
      registry.put(IMG_SUSPEND, ImageDescriptor.createFromFile(WorldWindow.class, "suspend.gif"));
      
      // TODO Connect4-Icon in SWT zeichnen
      loadTypeImage(registry, Connect4Controller.class, "icon.png");
      loadTypeImage(registry, WizardController.class, "icon.png");
    }
    
    private void loadTypeImage(ImageRegistry registry, Class<?> type, String path)
    {
      registry.put(type.getName(), ImageDescriptor.createFromFile(type, path));
    }
    
    @Override
    public FontRegistry getFontRegistry(WorldType type)
    {
      if (fonts == null)
        fonts = new HashMap<>();
        
      FontRegistry registry = fonts.get(type);
      
      if (registry == null)
      {
        fonts.put(type, registry = new FontRegistry());
      }
      
      return registry;
    }
  }
  
  public interface SharedImages
  {
    public final static String IMG_WORLD = "IMG_WORLD";
    
    public final static String IMG_RESUME  = "IMG_RESUME";
    public final static String IMG_SUSPEND = "IMG_SUSPEND";
    
    public ImageRegistry getImageRegistry(WorldType type);
    
    public default Image getImage(String key)
    {
      return getImageRegistry(null).get(key);
    }
    
    public default Image getImage(WorldType type, String key)
    {
      return getImageRegistry(type).get(key);
    }
    
    public default Image getImage(Class<?> type)
    {
      return getImageRegistry(null).get(type.getName());
    }
    
    public default ImageDescriptor getImageDescriptor(String key)
    {
      return getImageRegistry(null).getDescriptor(key);
    }
    
    public default ImageDescriptor getImageDescriptor(WorldType type, String key)
    {
      return getImageRegistry(type).getDescriptor(key);
    }
    
    public default ImageDescriptor getImageDescriptor(Class<?> type)
    {
      return getImageRegistry(null).getDescriptor(type.getName());
    }
  }
  
  public interface SharedFonts
  {
    public FontRegistry getFontRegistry(WorldType type);
    
    public default Font getFont(WorldType type, String key)
    {
      return getFontRegistry(type).get(key);
    }
    
    public default FontDescriptor getFontDescriptor(WorldType type, String key)
    {
      return getFontRegistry(type).getDescriptor(key);
    }
  }
}