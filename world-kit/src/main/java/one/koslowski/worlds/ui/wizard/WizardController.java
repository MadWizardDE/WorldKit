package one.koslowski.worlds.ui.wizard;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import one.koslowski.wizard.api.ScoreTable;
import one.koslowski.wizard.api.WizardPlayer;
import one.koslowski.wizard.api.WizardWorld;
import one.koslowski.wizard.api.strategy.RandomStrategy;
import one.koslowski.world.api.FrameDelimiter;
import one.koslowski.world.api.WorldManager;
import one.koslowski.world.api.invocation.StaticInvocationHandler;
import one.koslowski.worlds.ui.WorldController;

/**
 * TODOs:
 * 
 * - Events überarbeiten
 * 
 * -> WindowManager ?
 */
public class WizardController implements WorldController
{
  private WizardWorld world;

  public WizardController()
  {
    // Welt erzeugen
    WorldManager.sync(world = new WizardWorld(), () ->
    {
      WizardPlayer player1 = new WizardPlayer("Spieler 1");
      WizardPlayer player2 = new WizardPlayer("Spieler 2");
      WizardPlayer player3 = new WizardPlayer("Spieler 3");
      // WizardPlayer player4 = new WizardPlayer("Spieler 4");
      // WizardPlayer player5 = new WizardPlayer("Spieler 5");
      // WizardPlayer player6 = new WizardPlayer("Spieler 6");

      world.setDealer(player2);

      StaticInvocationHandler handler = new StaticInvocationHandler();
      handler.register(player1, new RandomStrategy());
      handler.register(player2, new RandomStrategy());
      handler.register(player3, new RandomStrategy());
      // handler.register(player4, new RandomStrategy());
      // handler.register(player5, new RandomStrategy());
      // handler.register(player6, new RandomStrategy());
      world.getEntityManager().setInvocationHandler(handler);
    });

    world.setFrameDelimiter(new FrameDelimiter(1));

    addPlayers(world);
  }

  public WizardController(WizardWorld world)
  {
    addPlayers(this.world = world);
  }

  private void addPlayers(WizardWorld world)
  {
    StaticInvocationHandler handler = new StaticInvocationHandler();
    for (WizardPlayer player : world.getPlayers())
      handler.register(player, new RandomStrategy());
    world.getEntityManager().setInvocationHandler(handler);
  }

  @Override
  public WizardWorld getWorld()
  {
    return world;
  }

  @Override
  public WizardControl createContents(Composite parent, MenuManager menuBar)
  {
    WizardControl control = new WizardControl(parent, menuBar, world);

    createScoreTable(control, WizardWorld.getContext().getScoreTable());
    createMenu(control, menuBar);

    world.addListener(control);

    return control;
  }

  private void createMenu(WizardControl control, MenuManager menuBar)
  {
    MenuManager viewMenu = new MenuManager("Ansicht", "view");
    viewMenu.setRemoveAllWhenShown(true);
    viewMenu.addMenuListener(manager ->
    {
      viewMenu.add(new ScoreTableAction(control));
    });
    menuBar.add(viewMenu);
    menuBar.updateAll(true);
  }

  private void createScoreTable(WizardControl control, ScoreTable table)
  {
    control.scoreWindow = new UIScoreTable(control.getShell(), table);
    control.eventBus.register(control.scoreWindow);
    control.scoreWindow.open();
    control.scoreWindow.getShell().addDisposeListener((event) ->
    {
      control.eventBus.unregister(control.scoreWindow);
      control.scoreWindow = null;
    });
  }

  @Override
  public void dispose(Control control, MenuManager menuBar)
  {
    WizardControl wizardControl = (WizardControl) control;

    disposeMenu(menuBar);

    if (wizardControl.scoreWindow != null)
      wizardControl.scoreWindow.close();

    world.removeListener(wizardControl);

    WorldController.super.dispose(control, menuBar);
  }

  private void disposeMenu(MenuManager menuBar)
  {
    menuBar.remove("view");
    menuBar.updateAll(true);
  }

  class ScoreTableAction extends Action
  {
    private WizardControl control;

    public ScoreTableAction(WizardControl control)
    {
      super("Block der Wahrheit", Action.AS_CHECK_BOX);

      this.control = control;

      setChecked(control.scoreWindow != null);
    }

    @Override
    public void run()
    {
      if (!isChecked())
      {
        control.scoreWindow.close();
      }
      else
      {
        WorldManager.sync(world, () -> createScoreTable(control, WizardWorld.getContext().getScoreTable()));
      }
    }
  }
}