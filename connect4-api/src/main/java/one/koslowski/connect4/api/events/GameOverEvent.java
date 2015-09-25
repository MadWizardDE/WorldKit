package one.koslowski.connect4.api.events;

import one.koslowski.connect4.api.Connect4Player;
import one.koslowski.connect4.api.Connect4World;
import one.koslowski.world.api.WorldEvent;

public class GameOverEvent extends WorldEvent
{
  private static final long serialVersionUID = 1L;

  private Connect4Player winner;

  public GameOverEvent(Connect4World source, Connect4Player winner)
  {
    super(source);

    this.winner = winner;
  }

  public Connect4Player getWinner()
  {
    return winner;
  }

}