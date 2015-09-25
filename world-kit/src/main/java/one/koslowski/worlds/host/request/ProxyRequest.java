package one.koslowski.worlds.host.request;

import one.koslowski.worlds.host.Request;

public class ProxyRequest extends Request<ProxyRequest.Response>
{
  private static final long serialVersionUID = 1L;

  private Request<?> payload;

  public ProxyRequest(Request<?> payload)
  {
    this.payload = payload;
  }

  public Request<?> getPayload()
  {
    return payload;
  }

  public class Response extends Request.Response
  {
    private static final long serialVersionUID = 1L;

  }
}