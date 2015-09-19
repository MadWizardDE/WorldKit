package one.koslowski.world.api;

import java.util.EventObject;

/**
 * Ein Event, dass vom Framework erzeugt wird und Meta-Informationen enthält.
 * 
 * Ist für Entities nicht sichtbar.
 */
public class SystemEvent extends EventObject
{
  private static final long serialVersionUID = 1L;
  
  public SystemEvent(Object source)
  {
    super(source);
  }
}