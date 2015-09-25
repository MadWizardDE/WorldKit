package one.koslowski.wizard.api.events;

import one.koslowski.wizard.api.WizardTrick;
import one.koslowski.world.api.WorldEvent;

/**
 * Ein Stich wurde gespielt.
 */
public class TrickPlayedEvent extends WorldEvent
{
  private static final long serialVersionUID = 1L;

  public TrickPlayedEvent(WizardTrick trick)
  {
    super(trick);
  }

  /**
   * @return der gespielte Stich
   */
  public WizardTrick getTrick()
  {
    return (WizardTrick) source;
  }

}