package one.koslowski.worlds.host.event;

import java.util.EventObject;

import one.koslowski.worlds.host.Link;

public class LinkEstablishedEvent extends EventObject
{
  private static final long serialVersionUID = 1L;

  public LinkEstablishedEvent(Link link)
  {
    super(link);
  }

  public Link getLink()
  {
    return (Link) source;
  }
}