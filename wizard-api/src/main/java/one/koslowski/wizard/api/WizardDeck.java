package one.koslowski.wizard.api;

import java.util.Collection;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import org.apache.commons.collections4.CollectionUtils;

import one.koslowski.wizard.api.WizardCard.Color;
import one.koslowski.wizard.api.events.DeckShuffeledEvent;
import one.koslowski.world.api.Entity;

public class WizardDeck extends Entity
{
  private static final long serialVersionUID = 1L;
  
  public static final int SIZE = 60;
  
  private Stack<WizardCard> stack;
  
  private boolean shuffled;
  
  {
    stack = new Stack<>();
  }
  
  /**
   * Erzeugt ein Standard-Deck.
   */
  WizardDeck()
  {
    // Farb-Karten (1-13)
    for (Color color : Color.values())
      for (int value = WizardCard.LOW_VALUE; value <= WizardCard.HIGH_VALUE; value++)
        stack.add(new WizardCard(color, value));
        
    // Sonderkarten (Narren und Zauberer)
    for (int nr = 1; nr <= 4; nr++)
    {
      stack.add(new WizardCard(WizardCard.MIN_VALUE, nr));
      stack.add(new WizardCard(WizardCard.MAX_VALUE, nr));
    }
  }
  
  List<WizardCard> getCards()
  {
    return stack;
  }
  
  public int getSize()
  {
    return stack.size();
  }
  
  public boolean isEmpty()
  {
    return stack.isEmpty();
  }
  
  /**
   * Karten mischen.
   */
  public void shuffle()
  {
    Collections.shuffle(stack);
    
    shuffled = true;
    
    publishEvent(new DeckShuffeledEvent(this));
  }
  
  /**
   * @return die oberste Karte vom Stapel
   * 
   * @throws IllegalStateException
   *           Karten sind nicht gemischt
   * @throws EmptyStackException
   *           Karten sind alle
   */
  public WizardCard draw() throws IllegalStateException, EmptyStackException
  {
    if (!shuffled)
      throw new IllegalStateException();
      
    return stack.pop();
  }
  
  public void collect(WizardCard card)
  {
    if (card != null)
    {
      stack.push(card);
      
      shuffled = false;
    }
  }
  
  public void collect(Collection<WizardCard> cards)
  {
    if (!CollectionUtils.isEmpty(cards))
    {
      stack.addAll(cards);
      
      shuffled = false;
    }
  }
}