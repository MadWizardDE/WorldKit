package one.koslowski.worlds.host;

import java.io.Serializable;

public abstract class Request<T extends Request.Response> implements Serializable
{
  private static final long serialVersionUID = 1L;

  transient RemoteHost host;

  transient T response;

  protected Request()
  {

  }

  public RemoteHost getHost()
  {
    return host;
  }

  public T getResponse()
  {
    if (response == null)
    {
      getClass().getTypeParameters(); // TODO create response
    }

    return response;
  }

  public static abstract class Response implements Serializable
  {
    private static final long serialVersionUID = 1L;

  }
}