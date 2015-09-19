package one.koslowski.world.api.event;

public interface ExceptionEvent
{
  public Object getSource();
  
  public Throwable getException();
}