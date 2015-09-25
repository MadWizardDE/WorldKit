package one.koslowski.worlds.host.event;

import java.util.EventObject;

import one.koslowski.worlds.host.RemoteHost;

public class HostOutOfSightEvent extends EventObject
{
  private static final long serialVersionUID = 1L;

  public HostOutOfSightEvent(RemoteHost source)
  {
    super(source);
  }

  public RemoteHost getHost()
  {
    return (RemoteHost) source;
  }
}