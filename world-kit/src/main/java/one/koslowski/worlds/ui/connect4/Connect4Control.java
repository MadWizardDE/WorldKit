package one.koslowski.worlds.ui.connect4;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import one.koslowski.connect4.api.Connect4Board;
import one.koslowski.connect4.api.Connect4Player;
import one.koslowski.connect4.api.Connect4Player.Connect4PlayerStrategy;
import one.koslowski.connect4.api.Connect4World;
import one.koslowski.connect4.api.UndoMoveException;
import one.koslowski.connect4.api.events.GameOverEvent;
import one.koslowski.connect4.api.events.MoveEvent;
import one.koslowski.world.api.WorldEventListener;
import one.koslowski.world.api.WaitException;
import one.koslowski.world.api.WorldEvent;
import one.koslowski.worlds.WorldKit;

class Connect4Control extends Canvas implements WorldEventListener, PaintListener
{
  private static final double CHIP_PADDING = 0.1;
  
  private final Color white = getDisplay().getSystemColor(SWT.COLOR_WHITE);
  private final Color black = getDisplay().getSystemColor(SWT.COLOR_BLACK);
  
  private EventBus eventBus;
  
  private Connect4World world;
  
  public Connect4Control(Composite parent, Connect4World world)
  {
    super(parent, SWT.DOUBLE_BUFFERED);
    
    this.world = world;
    
    eventBus = new EventBus();
    eventBus.register(this);
    
    addPaintListener(this);
    addMouseListener(new InputReceiver());
  }
  
  @Override
  public void paintControl(PaintEvent e)
  {
    Connect4Board board = world.getBoard(); // safe
    
    e.gc.setAntialias(SWT.ON);
    
    int x, y = 0;
    for (int boardY = 0; boardY < board.getHeight(); boardY++)
    {
      x = 0;
      for (int boardX = 0; boardX < board.getWidth(); boardX++)
      {
        Connect4Player player = board.get(boardX, boardY);
        
        Color color = player == null ? white : getPlayerColor(player);
        
        e.gc.setBackground(color);
        e.gc.fillOval(x + getChipPaddingX(), y + getChipPaddingY(),
            getChipSizeX(), getChipSizeY());
            
        e.gc.setLineWidth(2);
        e.gc.setForeground(black);
        e.gc.drawOval(x + getChipPaddingX(), y + getChipPaddingY(),
            getChipSizeX(), getChipSizeY());
            
        x += getChipAreaX();
      }
      y += getChipAreaY();
    }
  }
  
  @Override
  public void processEvent(WorldEvent event)
  {
    eventBus.post(event);
  }
  
  @Subscribe
  public void onMove(MoveEvent event)
  {
    Connect4Player player = event.getPlayer();
    
    WorldKit.UI.async(this, () ->
    {
      if (event.isIllegal())
      {
        MessageDialog.openInformation(getShell(), "Ungültiger Zug", player.getName() + " schummelt!");
      }
    });
  }
  
  @Subscribe
  public void onGameOver(GameOverEvent event)
  {
    Connect4Player winner = event.getWinner();
    
    WorldKit.UI.async(this, () ->
    {
      MessageDialog.openInformation(getShell(), "Game Over",
          winner == null
              ? "Unentschieden!"
              : event.getWinner().getName() + " hat das Spiel gewonnen!");
    });
  }
  
  private Color getPlayerColor(Connect4Player player)
  {
    switch (player.getColor())
    {
      case RED:
        return getDisplay().getSystemColor(SWT.COLOR_RED);
      case BLUE:
        return getDisplay().getSystemColor(SWT.COLOR_BLUE);
      case GREEN:
        return getDisplay().getSystemColor(SWT.COLOR_GREEN);
      case YELLOW:
        return getDisplay().getSystemColor(SWT.COLOR_YELLOW);
        
      default:
        throw new UnsupportedOperationException();
    }
  }
  
  private int getChipAreaX()
  {
    return getSize().x / world.getBoard().getWidth();
  }
  
  private int getChipAreaY()
  {
    return getSize().y / world.getBoard().getHeight();
  }
  
  private int getChipSizeX()
  {
    return (int) (getChipAreaX() * (1.0 - 2 * CHIP_PADDING));
  }
  
  private int getChipSizeY()
  {
    return (int) (getChipAreaY() * (1.0 - 2 * CHIP_PADDING));
  }
  
  private int getChipPaddingX()
  {
    return (int) (getChipAreaX() * CHIP_PADDING);
  }
  
  private int getChipPaddingY()
  {
    return (int) (getChipAreaY() * CHIP_PADDING);
  }
  
  private class InputReceiver implements Connect4PlayerStrategy, MouseListener
  {
    private Integer target;
    
    @Override
    public int move() throws UndoMoveException, WaitException
    {
      try
      {
        if (target == null)
          throw new WaitException(getEntity());
          
        if (target < 0)
          throw new UndoMoveException(getEntity());
          
        return target;
      }
      finally
      {
        target = null;
      }
    }
    
    @Override
    public void mouseUp(MouseEvent event)
    {
      for (Connect4Player player : world.getPlayers())
        if (world.isWaiting(player))
        {
          target = event.button == 3 ? -1 : event.x / getChipAreaX();
          world.notify(player);
          break;
        }
    }
    
    @Override
    public void mouseDown(MouseEvent e)
    {
    
    }
    
    @Override
    public void mouseDoubleClick(MouseEvent e)
    {
    
    }
  }
}