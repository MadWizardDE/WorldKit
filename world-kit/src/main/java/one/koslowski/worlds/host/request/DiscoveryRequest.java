package one.koslowski.worlds.host.request;

import one.koslowski.worlds.host.HostInfo;
import one.koslowski.worlds.host.Request;

public class DiscoveryRequest extends Request<DiscoveryRequest.Response>
{
  private static final long serialVersionUID = 1L;

  public DiscoveryRequest()
  {

  }

  public static class Response extends Request.Response
  {
    private static final long serialVersionUID = 1L;

    public HostInfo info;
  }
}