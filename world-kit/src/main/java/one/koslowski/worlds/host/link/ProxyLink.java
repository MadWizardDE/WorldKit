package one.koslowski.worlds.host.link;

import one.koslowski.worlds.host.Link;
import one.koslowski.worlds.host.Message;

public class ProxyLink extends Link
{
  private Link proxy;

  public ProxyLink(Link proxy)
  {
    this.proxy = proxy;
  }

  public Link getProxy()
  {
    return proxy;
  }

  @Override
  protected void sendMessage(Message message)
  {

  }

  public class ProxyLinkDescriptor extends LinkDescriptor<ProxyLink>
  {
    private static final long serialVersionUID = 1L;

    private LinkDescriptor<?> proxy;

    public ProxyLinkDescriptor(LinkDescriptor<?> proxy)
    {
      this.proxy = proxy;
    }

    public LinkDescriptor<?> getProxy()
    {
      return proxy;
    }
  }
}
