package one.koslowski.connect4.api;

/**
 * Durch das Werfen dieser Exception signalisiert der aktuelle Spieler die Wiederholung eines Zuges.
 */
public class UndoMoveException extends Exception
{
  private static final long serialVersionUID = 1L;

  /**
   * Spieler, dessen letzter Zug wiederholt werden soll.
   */
  Connect4Player player;

  /**
   * Anzahl an Zügen, die wiederholt werden sollen. (0 = kein Effekt)
   */
  Integer count;

  /**
   * @param player
   *          {@link #player}
   */
  public UndoMoveException(Connect4Player player)
  {
    this.player = player;
  }

  /**
   * @param count
   *          {@link #count}
   */
  public UndoMoveException(int count)
  {
    this.count = count;
  }
}