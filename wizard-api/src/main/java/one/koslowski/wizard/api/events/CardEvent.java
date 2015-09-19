package one.koslowski.wizard.api.events;

import one.koslowski.wizard.api.WizardCard;
import one.koslowski.world.api.Entity;
import one.koslowski.world.api.WorldEvent;

public abstract class CardEvent extends WorldEvent
{
  private static final long serialVersionUID = 1L;
  
  private Entity target;
  
  private WizardCard card;
  
  public CardEvent(Entity source, Entity target, WizardCard card)
  {
    super(source);
    
    this.target = target;
    
    this.card = card;
  }
  
  public Entity getSource()
  {
    return (Entity) source;
  }
  
  public Entity getTarget()
  {
    return (Entity) target;
  }
  
  public WizardCard getCard()
  {
    return card;
  }
  
}