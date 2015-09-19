package one.koslowski.wizard.api.events;

import one.koslowski.wizard.api.WizardCard;
import one.koslowski.wizard.api.WizardDeck;
import one.koslowski.wizard.api.WizardPlayer;

/**
 * Es wurde eine Karte gezogen.
 */
public class CardDrawnEvent extends CardEvent
{
  private static final long serialVersionUID = 1L;
  
  public CardDrawnEvent(WizardDeck deck, WizardPlayer player, WizardCard card)
  {
    super(deck, player, card);
  }
  
  /**
   * @return Deck, von dem die Karte gezogen wurde
   */
  @Override
  public WizardDeck getSource()
  {
    return (WizardDeck) super.getSource();
  }
  
  /**
   * @return Spieler, der die Karte gezogen hat (null = Trumpf-Karte)
   */
  @Override
  public WizardPlayer getTarget()
  {
    return (WizardPlayer) super.getTarget();
  }
  
}