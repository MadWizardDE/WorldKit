package one.koslowski.worlds.host;

import java.util.EventListener;
import java.util.EventObject;

public interface HostEventListener extends EventListener
{
  public void processEvent(EventObject event);
}