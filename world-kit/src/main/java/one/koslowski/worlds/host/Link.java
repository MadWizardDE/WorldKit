package one.koslowski.worlds.host;

import java.io.Serializable;

import one.koslowski.worlds.host.WorldSession.SessionInfo;

public abstract class Link
{
  LinkDescriptor<Link> desc;

  WorldSession session;

  protected Link()
  {

  }

  protected abstract void sendMessage(Message message);

  protected final void receiveMessage(Message message)
  {

  }

  void join(SessionInfo info)
  {

  }

  protected static abstract class LinkDescriptor<T extends Link> implements Serializable
  {
    private static final long serialVersionUID = 1L;

  }
}