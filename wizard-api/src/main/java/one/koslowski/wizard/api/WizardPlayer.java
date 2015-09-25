package one.koslowski.wizard.api;

import java.util.ArrayList;
import java.util.List;

import one.koslowski.world.api.Entity;

public class WizardPlayer extends Entity
{
  private static final long serialVersionUID = 1L;

  private String name;

  private List<WizardCard>  cards;
  private List<WizardTrick> tricks;

  {
    cards = new ArrayList<>(WizardDeck.SIZE / WizardWorld.MIN_PLAYERS);
    tricks = new ArrayList<>(WizardDeck.SIZE / WizardWorld.MAX_PLAYERS);
  }

  public WizardPlayer(String name)
  {
    this.name = name;
  }

  public String getName()
  {
    return name;
  }

  /**
   * @return Hand-Karten
   */
  public List<WizardCard> getCards()
  {
    return cards;
  }

  /**
   * @return gesammelte Stiche
   */
  public List<WizardTrick> getTricks()
  {
    return tricks;
  }

  public interface WizardPlayerStrategy extends EntityInvocationStrategy<WizardPlayer>
  {
    /**
     * @return darf diese Karte gespielt werden?
     */
    default boolean isPlayable(WizardTrick trick, WizardCard card)
    {
      return !trick.isReneging(getEntity(), card);
    }

    /**
     * Wird aufgerufen, nachdem alle Karten verteilt und wenn als Trumpf-Karte ein Zauberer
     * aufgedeckt wurde. Der Kartengeber darf in dem Fall die Trumpf-Farbe bestimmen.
     * 
     * @return die Trumpf-Farbe für diese Runde
     */
    WizardCard.Color defineTrumpColor() throws InterruptedException;

    /**
     * Wird am Anfang der Runde aufgerufen.
     * 
     * @return die vorhergesagte Anzahl an eigenen Stichen für diese Runde
     */
    int predictTricks() throws InterruptedException;

    /**
     * Wird pro Runde n-mal aufgerufen, wobei n für die Anzahl der Runden steht.
     * 
     * @return die Karte, die gespielt werden soll
     */
    WizardCard playCard() throws InterruptedException;
  }
}