package one.koslowski.worlds.host.link;

import one.koslowski.worlds.host.Link;
import one.koslowski.worlds.host.Message;

public class TCPLink extends Link
{
  public TCPLink()
  {

  }

  @Override
  protected void sendMessage(Message message)
  {

  }

  public static class TCPLinkDescriptor extends LinkDescriptor<TCPLink>
  {
    private static final long serialVersionUID = 1L;

  }
}