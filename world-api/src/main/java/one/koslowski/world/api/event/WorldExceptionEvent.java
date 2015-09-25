package one.koslowski.world.api.event;

import one.koslowski.world.api.WorldEvent;

public class WorldExceptionEvent extends WorldEvent implements ExceptionEvent
{
  private static final long serialVersionUID = 1L;

  private Throwable t;

  public WorldExceptionEvent(Object source, Throwable t)
  {
    super(source);

    this.t = t;
  }

  @Override
  public Throwable getException()
  {
    return t;
  }

}