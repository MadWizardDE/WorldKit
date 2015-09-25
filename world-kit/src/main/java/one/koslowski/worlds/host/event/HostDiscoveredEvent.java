package one.koslowski.worlds.host.event;

import java.util.EventObject;

import one.koslowski.worlds.host.RemoteHost;

public class HostDiscoveredEvent extends EventObject
{
  private static final long serialVersionUID = 1L;

  public HostDiscoveredEvent(RemoteHost host)
  {
    super(host);
  }

  public RemoteHost getHost()
  {
    return (RemoteHost) source;
  }
}