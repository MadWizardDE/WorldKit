package one.koslowski.connect4.api;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import one.koslowski.connect4.api.Connect4Board.Move;
import one.koslowski.connect4.api.Connect4Player.Connect4PlayerStrategy;
import one.koslowski.connect4.api.events.GameOverEvent;
import one.koslowski.connect4.api.events.GameStartedEvent;
import one.koslowski.connect4.api.events.MoveEvent;
import one.koslowski.world.api.Entity;
import one.koslowski.world.api.World;

public class Connect4World extends World
{
  private static final long serialVersionUID = 1L;
  
  public static final int DEFAULT_WIN = 4;
  
  private List<Connect4Player> players;
  
  private Connect4Board board;
  
  private Integer win;
  
  {
    players = new CopyOnWriteArrayList<>();
  }
  
  public Connect4World()
  {
    addContext(new Connect4Context());
    
    this.phase = () ->
    {
      if (board == null)
        board = new Connect4Board();
      if (win == null)
        win = DEFAULT_WIN;
      if (players.isEmpty())
        return null;
        
      publishEvent(new GameStartedEvent(this));
      
      return () -> this.play(getPlayer());
    };
  }
  
  public static Connect4Context getContext()
  {
    return getContext(Connect4Context.class);
  }
  
  public void setWin(int win)
  {
    if (win < 0)
      throw new IllegalArgumentException("win < 0");
      
    this.win = win;
  }
  
  public Connect4Board getBoard()
  {
    return board;
  }
  
  public List<Connect4Player> getPlayers()
  {
    return players;
  }
  
  public Connect4Player getPlayer()
  {
    if (players.isEmpty())
      return null;
      
    if (!board.history.isEmpty())
    {
      Connect4Player player = board.history.peek().player;
      
      int idxPlayer = players.indexOf(player);
      
      if (idxPlayer + 1 < players.size())
        return players.get(idxPlayer + 1);
    }
    
    return players.get(0); // der 1. Spieler
  }
  
  /**
   * Züge wiederholen.
   * 
   * @param player
   *          {@link UndoMoveException#player}
   * @param count
   *          {@link UndoMoveException#count}
   */
  private void undo(Connect4Player player, Integer count)
  {
    Move move = null;
    
    // hast du überhaupt schon einen Zug gemacht?
    if (player != null && board.getCount() < players.size())
      return;
      
    while (board.getCount() > 0 && (count == null || count > 0) && (move == null || move.player != player))
    {
      move = board.undo();
      
      publishEvent(new MoveEvent(move.player, move.x, move.y, null));
      
      count = count != null ? count - 1 : null;
    }
  }
  
  Phase play(Connect4Player player) throws InterruptedException
  {
    try
    {
      int x = ((Connect4PlayerStrategy) player.x).move(); // Zug berechnen
      
      int y = board.play(player, x);
      
      publishEvent(new MoveEvent(player, x, y, true));
      
      return this::check;
    }
    catch (UndoMoveException e)
    {
      undo(e.player, e.count);
    }
    catch (IllegalMoveException e)
    {
      publishEvent(new MoveEvent(player, e.x, null, false));
    }
    
    return () -> play(player); // Zug wiederholen
  }
  
  /**
   * Prüfen, ob ein Spieler gewonnen hat.
   */
  Phase check()
  {
    Connect4Player winner;
    if ((winner = board.getLine(win)) != null || board.isFull())
    {
      publishEvent(new GameOverEvent(this, winner));
      
      return null;
    }
    
    return () -> this.play(getPlayer());
  }
  
  @Override
  protected void addEntity(Entity entity)
  {
    if (entity instanceof Connect4Board)
    {
      if (getEntityContext().count(Connect4Board.class) > 0)
        throw new IllegalStateException();
        
      board = (Connect4Board) entity;
    }
    
    if (entity instanceof Connect4Player)
    {
      if (getState() != WorldState.FRESH)
        throw new IllegalStateException();
        
      players.add((Connect4Player) entity);
    }
    
    super.addEntity(entity);
  }
  
  @Override
  public String toString()
  {
    return getClass().getSimpleName() + "[chips = " + board.getCount() + "]";
  }
  
  public class Connect4Context extends WorldContext<Connect4World>
  {
    public List<Connect4Player> getPlayers()
    {
      return players;
    }
    
    public Connect4Board getBoard()
    {
      return board;
    }
    
    public Integer getWin()
    {
      return win;
    }
  }
}