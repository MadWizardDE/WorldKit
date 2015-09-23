package one.koslowski.worlds.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.CoolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.WindowManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import one.koslowski.connect4.api.Connect4World;
import one.koslowski.wizard.api.WizardWorld;
import one.koslowski.world.api.FrameDelimiter;
import one.koslowski.world.api.World;
import one.koslowski.world.api.World.WorldState;
import one.koslowski.world.api.WorldEvent;
import one.koslowski.world.api.WorldEventListener;
import one.koslowski.world.api.WorldManager;
import one.koslowski.world.api.event.WorldStateEvent;
import one.koslowski.worlds.WorldKit;
import one.koslowski.worlds.WorldType;
import one.koslowski.worlds.ui.connect4.Connect4Controller;
import one.koslowski.worlds.ui.wizard.WizardController;

public class WorldWindow extends org.eclipse.jface.window.ApplicationWindow
{
  WorldController    controller;
  WorldEventListener listener;
  
  // +++ Controls +++ //
  
  Composite container;
  Control   control;
  
  // +++ Actions +++ //
  
  SaveAction     actionSave;
  PlayAction     actionPlay;
  PauseAction    actionPause;
  SpeedUpAction  actionSpeedUp;
  SlowDownAction actionSlowDown;
  
  public WorldWindow()
  {
    this(null);
  }
  
  public WorldWindow(WorldController controller)
  {
    super(null);
    
    this.controller = controller;
    
    // setShellStyle(SWT.SHELL_TRIM);
    
    addMenuBar();
    // addToolBar(SWT.FLAT);
    addToolBar(SWT.BORDER | SWT.FLAT);
    // addCoolBar(SWT.FLAT | SWT.BORDER);
  }
  
  @Override
  public void create()
  {
    super.create();
    
    if (getCoolBarManager() != null)
      getCoolBarManager().setLockLayout(true);
  }
  
  @Override
  protected Point getInitialSize()
  {
    return new Point(500, 500);
  }
  
  @Override
  protected void configureShell(Shell shell)
  {
    if (shell != null)
      super.configureShell(shell);
    else
      shell = getShell();
      
    if (controller != null)
    {
      shell.setImage(WorldKit.UI.getImage(controller.getClass()));
      
      shell.setText(WorldType.of(controller.getWorld()).getName());
    }
    else
    {
      shell.setImage(WorldKit.SharedImages.WORLD.getImage());
      
      shell.setText("Keine Welt geladen");
    }
  }
  
  @Override
  protected MenuManager createMenuManager()
  {
    MenuManager main = super.createMenuManager();
    
    MenuManager worldMenu = new MenuManager("Welt");
    worldMenu.setRemoveAllWhenShown(true);
    worldMenu.addMenuListener(manager ->
    {
      MenuManager newMenu = new MenuManager("Neu");
      for (WorldType type : WorldType.values())
        newMenu.add(new NewWorldAction(type));
      worldMenu.add(newMenu);
      worldMenu.add(new LoadWorldAction());
      worldMenu.add(new ConnectWorldAction());
      
      // Welt(en) schließen
      if (controller != null)
      {
        worldMenu.add(new Separator());
        
        worldMenu.add(new CloseWorldAction());
        worldMenu.add(new CloseAllWorldsAction());
        
        worldMenu.add(new Separator());
        
        worldMenu.add(new PropertiesAction());
      }
      
      // Welt-Liste
      WorldController[] controllers = getWindowManager().getControllers();
      if (controllers.length != 0)
      {
        worldMenu.add(new Separator());
        
        for (WorldController controller : controllers)
        {
          WorldAction worldAction = new WorldAction(controller);
          
          worldMenu.add(worldAction);
        }
      }
      
      worldMenu.add(new Separator());
      
      // Beenden
      worldMenu.add(new ExitAction());
    });
    main.add(worldMenu);
    
    return main;
  }
  
  @Override
  protected CoolBarManager createCoolBarManager(int style)
  {
    CoolBarManager manager = super.createCoolBarManager(style);
    
    manager.add(createToolBarManager(SWT.NONE));
    
    return manager;
  }
  
  @Override
  protected ToolBarManager createToolBarManager(int style)
  {
    ToolBarManager manager = super.createToolBarManager(style);
    
    manager.add(actionSave = new SaveAction());
    
    manager.add(new Separator());
    
    manager.add(actionPlay = new PlayAction());
    manager.add(actionPause = new PauseAction());
    
    manager.add(new Separator());
    
    manager.add(actionSlowDown = new SlowDownAction());
    manager.add(actionSpeedUp = new SpeedUpAction());
    
    return manager;
  }
  
  @Override
  protected Control createContents(Composite parent)
  {
    Composite box = new Composite(parent, SWT.NONE);
    GridLayout l = new GridLayout(1, true);
    {
      l.marginWidth = l.marginHeight = l.horizontalSpacing = l.verticalSpacing = 0;
    }
    box.setLayout(l);
    
    // Label separator = new Label(box, SWT.SEPARATOR | SWT.HORIZONTAL);
    // separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    
    container = new Composite(box, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));
    container.setLayout(new FillLayout());
    
    if (controller != null)
    {
      control = createWorldContents();
    }
    
    return container;
  }
  
  protected Control createWorldContents()
  {
    return WorldManager.sync(controller.getWorld(), () -> controller.createContents(container, getMenuBarManager()));
  }
  
  private void updateToolBar()
  {
    actionSave.update();
    actionPlay.update();
    actionPause.update();
    actionSpeedUp.update();
    actionSlowDown.update();
  }
  
  @Override
  public WorldWindowManager getWindowManager()
  {
    return (WorldWindowManager) super.getWindowManager();
  }
  
  @Override
  public void setWindowManager(WindowManager manager)
  {
    if (manager != null && !(manager instanceof WorldWindowManager))
      throw new IllegalArgumentException();
    super.setWindowManager(manager);
  }
  
  public WorldController getController()
  {
    return controller;
  }
  
  public void setController(WorldController controller)
  {
    if (getShell() != null)
    {
      if (this.controller != null)
      {
        // Listener entfernen
        this.controller.getWorld().removeListener(listener);
        this.listener = null;
        
        // altes Control entfernen
        this.controller.dispose(control, getMenuBarManager());
        this.control = null;
      }
      
      this.controller = controller;
      
      if (this.controller != null)
      {
        // neues Control erzeugen
        this.control = createWorldContents();
        
        // Listener hinzufügen
        this.controller.getWorld().addListener(listener = new WorldListener());
      }
      
      configureShell(null);
      updateToolBar();
      
      container.layout();
      container.redraw();
    }
  }
  
  @Override
  public boolean close()
  {
    if (controller != null)
    {
      // TODO im Single Window-Mode OK
      new CloseAllWorldsAction().run();
      controller = null;
    }
    
    return super.close();
  }
  
  private class WorldListener implements WorldEventListener
  {
    private EventBus bus = new EventBus();
    
    private WorldListener()
    {
      bus.register(this);
    }
    
    @Override
    public void processEvent(WorldEvent event)
    {
      bus.post(event);
    }
    
    @Subscribe
    public void onWorldStateChange(WorldStateEvent event)
    {
      WorldKit.UI.async(control, () ->
      {
        try
        {
          updateToolBar();
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      });
    }
  }
  
  class NewWorldAction extends Action
  {
    private WorldType type;
    
    public NewWorldAction(WorldType type)
    {
      super(type.getName());
      
      this.type = type;
      
      if (type == WorldType.CONWAYS)
        setEnabled(false);
    }
    
    @Override
    public void run()
    {
      WorldController controller;
      
      switch (type)
      {
        case CONNECT4:
          controller = new Connect4Controller();
          break;
          
        case WIZARD:
          controller = new WizardController();
          break;
          
        default:
          return;
      }
      
      getWindowManager().addController(controller);
    }
  }
  
  class LoadWorldAction extends Action
  {
    public LoadWorldAction()
    {
      super("Laden...");
    }
    
    @Override
    public void run()
    {
      FileDialog fileDialog = new FileDialog(getShell(), SWT.OPEN);
      fileDialog.setFilterExtensions(new String[] { "*.world" });
      fileDialog.setText("Laden...");
      fileDialog.open();
      
      if (!fileDialog.getFileName().isEmpty())
      {
        File file = new File(fileDialog.getFilterPath() + "/"
            + fileDialog.getFileName());
            
        try (FileInputStream input = new FileInputStream(file))
        {
          World world = WorldManager.read(input);
          
          WorldController controller;
          
          switch (WorldType.of(world))
          {
            case CONNECT4:
              controller = new Connect4Controller((Connect4World) world);
              break;
              
            case WIZARD:
              controller = new WizardController((WizardWorld) world);
              break;
              
            default:
              throw new ClassNotFoundException();
          }
          
          getWindowManager().addController(controller);
        }
        catch (ClassNotFoundException | IOException e)
        {
          MessageDialog.openError(getShell(), "Fehler", "Laden fehlgeschlagen");
          
          e.printStackTrace();
        }
      }
    }
  }
  
  class ConnectWorldAction extends Action
  {
    public ConnectWorldAction()
    {
      super("Verbinden...");
    }
    
    @Override
    public void run()
    {
    
    }
  }
  
  class CloseWorldAction extends Action
  {
    public CloseWorldAction()
    {
      super("Schließen");
      
      setAccelerator(SWT.CTRL | 'W');
      setEnabled(controller != null);
    }
    
    @Override
    public void run()
    {
      getWindowManager().removeController(controller);
    }
  }
  
  class CloseAllWorldsAction extends Action
  {
    public CloseAllWorldsAction()
    {
      super("Alle schließen");
      
      setAccelerator(SWT.CTRL | SWT.SHIFT | 'W');
      setEnabled(controller != null);
    }
    
    @Override
    public void run()
    {
      for (WorldController controller : getWindowManager().getControllers())
      {
        getWindowManager().removeController(controller);
      }
    }
  }
  
  class SaveAction extends Action
  {
    public SaveAction()
    {
      super("&Speichern");
      
      setImageDescriptor(WorldKit.SharedImages.SAVE.getDescriptor());
      
      setAccelerator(SWT.CTRL | 'S');
      
      update();
    }
    
    private void update()
    {
      setEnabled(enabled());
    }
    
    private boolean enabled()
    {
      if (controller == null)
        return false;
        
      return !controller.getWorld().getState().isRunning();
    }
    
    @Override
    public void run()
    {
      FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
      fileDialog.setFilterExtensions(new String[] { "*.world" });
      fileDialog.setText("Speichern...");
      fileDialog.open();
      
      if (!fileDialog.getFileName().isEmpty())
      {
        File file = new File(fileDialog.getFilterPath() + "/"
            + fileDialog.getFileName());
            
        try (FileOutputStream output = new FileOutputStream(file))
        {
          WorldManager.write(controller.getWorld(), output);
        }
        catch (IOException e)
        {
          MessageDialog.openError(getShell(), "Fehler", "Speichern fehlgeschlagen");
          
          e.printStackTrace();
          
          // TODO StatusManager
          // StatusManager.getManager().handle(new Status(IStatus.ERROR, null, "Speichern
          // fehlgeschlagen.", e),
          // StatusManager.BLOCK);
        }
      }
    }
  }
  
  class PlayAction extends Action
  {
    public PlayAction()
    {
      super("Starten", SWT.NONE);
      
      setImageDescriptor(WorldKit.SharedImages.RESUME.getDescriptor());
      
      update();
    }
    
    private void update()
    {
      setEnabled(enabled());
    }
    
    private boolean enabled()
    {
      if (controller == null)
        return false;
      else
        switch (controller.getWorld().getState())
        {
          case FRESH:
          case EXCEPTION:
          case INTERRUPTED:
            return true;
            
          default:
            return false;
        }
    }
    
    @Override
    public void run()
    {
      WorldManager.sync(controller.getWorld(), () ->
      {
        if (enabled())
        {
          controller.getWorld().getManager().execute(controller.getWorld());
        }
      });
    }
  }
  
  class PauseAction extends Action
  {
    public PauseAction()
    {
      super("Anhalten", SWT.NONE);
      
      setImageDescriptor(WorldKit.SharedImages.SUSPEND.getDescriptor());
      
      update();
    }
    
    private void update()
    {
      setEnabled(enabled());
    }
    
    private boolean enabled()
    {
      if (controller == null)
        return false;
      else
        switch (controller.getWorld().getState())
        {
          case WAITING:
          case EXECUTING:
          case THROTTLING:
            return true;
            
          default:
            return false;
        }
    }
    
    @Override
    public void run()
    {
      WorldManager.sync(controller.getWorld(), () ->
      {
        if (enabled())
        {
          controller.getWorld().interrupt();
        }
      });
    }
  }
  
  class SpeedUpAction extends Action
  {
    public SpeedUpAction()
    {
      super("Schneller", SWT.NONE);
      
      setImageDescriptor(WorldKit.SharedImages.FASTER.getDescriptor());
      
      setAccelerator(SWT.CTRL | '+');
      
      update();
    }
    
    private void update()
    {
      setEnabled(enabled());
    }
    
    private boolean enabled()
    {
      if (controller == null || controller.getWorld().getFrameDelimiter() == null)
        return false;
        
      return controller.getWorld().getState() != WorldState.STOPPED;
    }
    
    @Override
    public void run()
    {
      WorldManager.sync(controller.getWorld(), () ->
      {
        FrameDelimiter d = controller.getWorld().getFrameDelimiter();
        
        d.setFPS(d.getFPS() * 2);
      });
    }
  }
  
  class SlowDownAction extends Action
  {
    public SlowDownAction()
    {
      super("Langsamer", SWT.NONE);
      
      setImageDescriptor(WorldKit.SharedImages.SLOWER.getDescriptor());
      
      setAccelerator(SWT.CTRL | '-');
      
      update();
    }
    
    private void update()
    {
      setEnabled(enabled());
    }
    
    private boolean enabled()
    {
      if (controller == null || controller.getWorld().getFrameDelimiter() == null)
        return false;
        
      return controller.getWorld().getState() != WorldState.STOPPED;
    }
    
    @Override
    public void run()
    {
      WorldManager.sync(controller.getWorld(), () ->
      {
        FrameDelimiter d = controller.getWorld().getFrameDelimiter();
        
        d.setFPS(d.getFPS() / 2);
      });
    }
  }
  
  class PropertiesAction extends Action
  {
    public PropertiesAction()
    {
      super("Eigenschaften", SWT.NONE);
      
      setAccelerator(SWT.ALT | SWT.CR);
      setEnabled(false);
    }
    
    @Override
    public void run()
    {
    
    }
  }
  
  class WorldAction extends Action
  {
    private WorldController controller;
    
    public WorldAction(WorldController controller)
    {
      super(WorldType.of(controller.getWorld()).getName(), SWT.RADIO);
      
      this.controller = controller;
      
      setImageDescriptor(WorldKit.UI.getImageDescriptor(controller.getClass()));
      setChecked(WorldWindow.this.controller == controller);
    }
    
    @Override
    public void run()
    {
      setController(controller);
    }
  }
  
  class ExitAction extends Action
  {
    public ExitAction()
    {
      super("Beenden");
    }
    
    @Override
    public void run()
    {
      getWindowManager().close();
    }
  }
  
  public class WorldWindowContext
  {
  
  }
}