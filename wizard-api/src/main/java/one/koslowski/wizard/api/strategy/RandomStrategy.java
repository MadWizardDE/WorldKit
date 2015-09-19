package one.koslowski.wizard.api.strategy;

import java.util.Random;

import one.koslowski.wizard.api.WizardCard;
import one.koslowski.wizard.api.WizardCard.Color;
import one.koslowski.wizard.api.WizardPlayer.WizardPlayerStrategy;
import one.koslowski.wizard.api.WizardTrick;
import one.koslowski.wizard.api.WizardWorld;

public class RandomStrategy implements WizardPlayerStrategy
{
  private Random random = new Random();
  
  @Override
  public Color defineTrumpColor()
  {
    // zufällige Trumpf-Farbe
    return Color.values()[random.nextInt(Color.values().length)];
  }
  
  @Override
  public int predictTricks()
  {
    // zufällige Anzahl an Stichen
    return random.nextInt(getEntity().getCards().size() + 1);
  }
  
  @Override
  public WizardCard playCard()
  {
    WizardTrick trick = WizardWorld.getContext().getTrick();
    
    // zufällige Hand-Karte
    WizardCard card = getEntity().getCards().get(random.nextInt(getEntity().getCards().size()));
    
    // wir spielen regelkonform
    return isPlayable(trick, card) ? card : playCard();
  }
}