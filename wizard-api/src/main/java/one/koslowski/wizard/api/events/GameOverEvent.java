package one.koslowski.wizard.api.events;

import one.koslowski.wizard.api.WizardPlayer;
import one.koslowski.wizard.api.WizardWorld;
import one.koslowski.world.api.WorldEvent;

/**
 * Das Spiel ist vorbei.
 */
public class GameOverEvent extends WorldEvent
{
  private static final long serialVersionUID = 1L;

  private WizardPlayer winner;

  public GameOverEvent(WizardWorld source, WizardPlayer winner)
  {
    super(source);

    this.winner = winner;
  }

  /**
   * @return Sieger (null = unentschieden)
   */
  public WizardPlayer getWinner()
  {
    return winner;
  }

}