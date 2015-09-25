package one.koslowski.wizard.api.events;

import one.koslowski.wizard.api.WizardCard;
import one.koslowski.wizard.api.WizardPlayer;
import one.koslowski.world.api.WorldEvent;

/**
 * Es wurde eine Trumpf-Farbe bestimmt.
 */
public class TrumpColorEvent extends WorldEvent
{
  private static final long serialVersionUID = 1L;

  private WizardCard.Color color;

  public TrumpColorEvent(Object source, WizardCard.Color color)
  {
    super(source);

    this.color = color;
  }

  /**
   * @return Spieler, der die Trumpf-Farbe bestimmt hat (null = automatisch)
   */
  public WizardPlayer getPlayer()
  {
    return source instanceof WizardPlayer ? (WizardPlayer) source : null;
  }

  /**
   * @return Trumpf-Farbe für diese Runde (null = kein Trumpf)
   */
  public WizardCard.Color getColor()
  {
    return color;
  }

}