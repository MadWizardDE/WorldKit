package one.koslowski.worlds.host.message;

import one.koslowski.worlds.host.Message;

public class ProxyMessage extends Message
{
  private static final long serialVersionUID = 1L;

  private Message payload;

  public ProxyMessage(Message payload)
  {
    this.payload = payload;
  }

  public Message getPayload()
  {
    return payload;
  }
}
