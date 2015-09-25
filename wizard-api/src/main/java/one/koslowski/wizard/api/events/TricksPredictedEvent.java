package one.koslowski.wizard.api.events;

import one.koslowski.wizard.api.WizardPlayer;
import one.koslowski.world.api.WorldEvent;

/**
 * Es wurde eine Anzahl Stiche vorhergesagt.
 */
public class TricksPredictedEvent extends WorldEvent
{
  private static final long serialVersionUID = 1L;

  /** die Anzahl der Stiche */
  public final int tricks;

  public TricksPredictedEvent(WizardPlayer source, int tricks)
  {
    super(source);

    this.tricks = tricks;
  }

  /**
   * @return Spieler, der die Vorhersage getroffen hat
   */
  public WizardPlayer getSource()
  {
    return (WizardPlayer) super.getSource();
  }

  /**
   * @return {@link #tricks}
   */
  public int getTricks()
  {
    return tricks;
  }

}