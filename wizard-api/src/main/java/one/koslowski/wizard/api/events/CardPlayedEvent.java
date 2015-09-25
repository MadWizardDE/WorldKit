package one.koslowski.wizard.api.events;

import one.koslowski.wizard.api.WizardCard;
import one.koslowski.wizard.api.WizardPlayer;
import one.koslowski.wizard.api.WizardTrick;

/**
 * Es wurde eine Karte gespielt.
 */
public class CardPlayedEvent extends CardEvent
{
  private static final long serialVersionUID = 1L;

  public CardPlayedEvent(WizardPlayer player, WizardTrick trick, WizardCard card)
  {
    super(player, trick, card);
  }

  /**
   * @return Spieler, der die Karte gespielt hat
   */
  @Override
  public WizardPlayer getSource()
  {
    return (WizardPlayer) super.getSource();
  }

  /**
   * @return der aktuelle Stich
   */
  @Override
  public WizardTrick getTarget()
  {
    return (WizardTrick) super.getTarget();
  }

}