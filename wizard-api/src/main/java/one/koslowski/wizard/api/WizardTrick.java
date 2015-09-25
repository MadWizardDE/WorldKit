package one.koslowski.wizard.api;

import java.util.AbstractList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;

import one.koslowski.world.api.Entity;

public class WizardTrick extends Entity
{
  private static final long serialVersionUID = 1L;

  /** Diese Farbe ist Trumpf. */
  private WizardCard.Color trumps;
  /** Diese Farbe muss bedient werden. */
  private WizardCard.Color suits;

  /** Die bisher gespielten Karten. */
  private Map<WizardPlayer, WizardCard> cards;

  /** Diesem Spieler gehört bisher der Stich. */
  private WizardPlayer player;

  {
    cards = new LinkedHashMap<>();
  }

  WizardTrick(WizardCard.Color trumpColor)
  {
    this.trumps = trumpColor;
  }

  /**
   * @return {@link #trumps}
   */
  public WizardCard.Color getTrumpColor()
  {
    return trumps;
  }

  /**
   * @return {@link #suits}
   */
  public WizardCard.Color getSuitColor()
  {
    return suits;
  }

  /**
   * @return {@link #cards}
   */
  public List<WizardCard> getCards()
  {
    return new AbstractList<WizardCard>()
    {
      @Override
      public WizardCard get(int index)
      {
        return CollectionUtils.get(cards.values(), index);
      }

      @Override
      public int size()
      {
        return cards.size();
      }
    };
  }

  public WizardCard getCard(WizardPlayer player)
  {
    return cards.get(player);
  }

  /**
   * @return {@link #player}
   */
  public WizardPlayer getPlayer()
  {
    return player;
  }

  /**
   * @return muss der Spieler mit einer anderen Karte bedienen?
   */
  boolean isReneging(WizardPlayer player, WizardCard card)
  {
    if (suits != null && card.getColor() != null)
      if (card.getColor() != suits)
        for (WizardCard playerCard : player.getCards())
          if (playerCard.getColor() == suits)
            return true;

    return false;
  }

  /**
   * @throws IllegalMoveException
   *           Spieler hat nicht bedient (obwohl er müsste)
   */
  void play(WizardPlayer player, WizardCard card) throws IllegalMoveException
  {
    if (isReneging(player, card))
      throw new IllegalMoveException();

    this.cards.put(player, card); // OK

    if (this.player == null)
    {
      this.player = player;

      suits = card.getColor();
    }
    else
    {
      // nur Karten ohne oder mit der angespielten- oder Trumpffarbe können gewinnen
      if (card.getColor() == null || card.getColor() == trumps || card.getColor() == suits || suits == null)
      {
        // Karte mit höherem Wert gewinnt
        WizardCard playerCard = cards.get(this.player);
        if (card.getColor() == trumps && playerCard.getColor() != trumps && playerCard.getColor() != null
            || card.getValue() > playerCard.getValue())
        {
          this.player = player;

          if (suits == null)
          {
            suits = card.getColor(); // ab jetzt muss bedient werden
          }
        }
      }
    }
  }
}