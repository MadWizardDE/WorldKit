package one.koslowski.worlds.host.message;

import java.util.List;

import one.koslowski.worlds.host.Message;
import one.koslowski.worlds.host.WorldSession.SessionInfo;

public class SessionListMessage extends Message
{
  private static final long serialVersionUID = 1L;

  private List<SessionInfo> list;

  public SessionListMessage()
  {

  }

}