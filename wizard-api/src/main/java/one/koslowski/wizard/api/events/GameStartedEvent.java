package one.koslowski.wizard.api.events;

import one.koslowski.wizard.api.WizardWorld;
import one.koslowski.world.api.WorldEvent;

/**
 * Das Spiel wurde gestartet.
 */
public class GameStartedEvent extends WorldEvent
{
  private static final long serialVersionUID = 1L;
  
  public GameStartedEvent(WizardWorld source)
  {
    super(source);
  }
  
}