package one.koslowski.worlds.ui.connect4;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import one.koslowski.connect4.api.Connect4Board;
import one.koslowski.connect4.api.Connect4Player;
import one.koslowski.connect4.api.Connect4Player.Color;
import one.koslowski.connect4.api.Connect4World;
import one.koslowski.connect4.api.strategy.RandomStrategy;
import one.koslowski.world.api.FrameDelimiter;
import one.koslowski.world.api.WorldManager;
import one.koslowski.world.api.invocation.StaticInvocationHandler;
import one.koslowski.worlds.ui.WorldController;

public class Connect4Controller implements WorldController
{
  private Connect4World world;
  
  public Connect4Controller()
  {
    // Welt erzeugen
    WorldManager.sync(world = new Connect4World(), () ->
    {
      new Connect4Board(7, 6);
      
      new Connect4Player("Rot", Color.RED);
      new Connect4Player("Gelb", Color.YELLOW);
      
      // System.loadLibrary("Velena");
      // new VelenaPlayer("Rot", Color.RED, Difficulty.STRONG);
      // new VelenaPlayer("Gelb", Color.YELLOW, Difficulty.STRONG);
      
      // new HumanPlayer("Rot", Color.RED);
      // new HumanPlayer("Gelb", Color.YELLOW);
    });
    
    world.setFrameDelimiter(new FrameDelimiter(2));
    
    setWorld(world);
  }
  
  public Connect4Controller(Connect4World world)
  {
    setWorld(world);
  }
  
  private void setWorld(Connect4World world)
  {
    this.world = world;
    
    StaticInvocationHandler handler = new StaticInvocationHandler();
    for (Connect4Player player : world.getPlayers())
      handler.register(player, new RandomStrategy());
    world.getEntityManager().setInvocationHandler(handler);
  }
  
  @Override
  public Connect4World getWorld()
  {
    return world;
  }
  
  @Override
  public Connect4Control createContents(Composite parent, MenuManager menuBar)
  {
    Connect4Control control = new Connect4Control(parent, world);
    
    world.addListener(control);
    
    return control;
  }
  
  @Override
  public void dispose(Control control, MenuManager menuBar)
  {
    world.removeListener((Connect4Control) control);
    
    WorldController.super.dispose(control, menuBar);
  }
}