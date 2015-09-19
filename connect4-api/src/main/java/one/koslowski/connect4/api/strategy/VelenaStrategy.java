package one.koslowski.connect4.api.strategy;

import com.google.common.eventbus.Subscribe;

import one.koslowski.connect4.api.Connect4Board;
import one.koslowski.connect4.api.Connect4Player;
import one.koslowski.connect4.api.Connect4Player.Connect4PlayerStrategy;
import one.koslowski.connect4.api.Connect4World;
import one.koslowski.connect4.api.events.GameStartedEvent;
import one.koslowski.connect4.api.events.MoveEvent;

/**
 * Perfekter "Vier gewinnt"-Spieler (auf Difficulty.STRONG), der die Velena.dll anbindet.
 * 
 * TODO Speicherleck fixen
 */
public class VelenaStrategy implements Connect4PlayerStrategy
{
  private Difficulty difficulty;
  
  /** "Gedächtnis" - alle (bisherigen) Züge als Zahlenfolge von 1-7 */
  private String moveCode;
  
  public VelenaStrategy(Difficulty difficulty)
  {
    this.difficulty = difficulty;
  }
  
  @Subscribe
  public void onStart(GameStartedEvent event)
  {
    Connect4Board board = Connect4World.getContext().getBoard();
    
    // Velena funktioniert nur bei 2 Spielern ...
    if (getContext().getEntities(Connect4Player.class).size() != 2)
      throw new IllegalStateException("players != 2");
    // ... 7x6 Spielfeldern ...
    if (board.getWidth() != Connect4Board.DEFAULT_WIDTH
        || board.getHeight() != Connect4Board.DEFAULT_HEIGHT)
      throw new IllegalStateException("board != 7x6");
    // ... und genau 4 Sieg-Chips
    if (Connect4World.getContext().getWin() != 4)
      throw new IllegalStateException("win != 4");
      
    moveCode = new String();
  }
  
  @Subscribe
  public void onMove(MoveEvent move)
  {
    if (!move.isIllegal())
      if (!move.isUndo())
        moveCode += Integer.toString(move.getX() + 1);
      else
        moveCode = moveCode.substring(0, moveCode.length() - 1);
  }
  
  @Override
  public native int move();
  
  public enum Difficulty
  {
    WEAK('a'), NORMAL('b'), STRONG('c');
    
    public final char id;
    
    private Difficulty(char id)
    {
      this.id = id;
    }
  }
}