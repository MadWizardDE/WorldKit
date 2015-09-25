package one.koslowski.wizard.api.events;

import one.koslowski.wizard.api.WizardCard;
import one.koslowski.wizard.api.WizardPlayer;
import one.koslowski.wizard.api.WizardTrick;

/**
 * Ein Spieler hat versucht gegen die Regeln zu verstoßen.
 */
public class PlayerCheatedEvent extends CardEvent
{
  private static final long serialVersionUID = 1L;

  public PlayerCheatedEvent(WizardPlayer player, WizardTrick trick, WizardCard card)
  {
    super(player, trick, card);
  }

  /**
   * @return Spieler, der versucht hat zu mogeln
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