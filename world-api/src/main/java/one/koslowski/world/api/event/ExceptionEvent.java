package one.koslowski.world.api.event;

public interface ExceptionEvent extends SystemEvent
{
  public Object getSource();
  
  public Throwable getException();
}