package one.koslowski.worlds.host.event;

import java.util.EventObject;

public abstract class SessionEvent extends EventObject
{
  private static final long serialVersionUID = 1L;

  public SessionEvent(Object source)
  {
    super(source);
  }
}