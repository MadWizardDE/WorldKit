package one.koslowski.wizard.api.events;

import one.koslowski.wizard.api.WizardDeck;
import one.koslowski.world.api.EntityEvent;

/**
 * Die Karten wurden gemischt. (am Anfang jeder Runde)
 */
public class DeckShuffeledEvent extends EntityEvent
{
  private static final long serialVersionUID = 1L;

  public DeckShuffeledEvent(WizardDeck source)
  {
    super(source);
  }

  public WizardDeck getDeck()
  {
    return (WizardDeck) source;
  }
}