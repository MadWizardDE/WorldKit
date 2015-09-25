package one.koslowski.worlds.host.request;

import one.koslowski.worlds.host.HostInfo;
import one.koslowski.worlds.host.Request;

public class ConnectRequest extends Request<ConnectRequest.Response>
{
  private static final long serialVersionUID = 1L;

  public HostInfo info;

  public ConnectRequest()
  {

  }

  public static class Response extends Request.Response
  {
    private static final long serialVersionUID = 1L;

    public boolean successful;

    public HostInfo info;

  }
}