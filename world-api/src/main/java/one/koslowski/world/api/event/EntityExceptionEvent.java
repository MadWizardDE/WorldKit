package one.koslowski.world.api.event;

import one.koslowski.world.api.EntityEvent;
import one.koslowski.world.api.exception.EntityException;

public class EntityExceptionEvent extends EntityEvent implements ExceptionEvent
{
  private static final long serialVersionUID = 1L;
  
  private EntityException e;
  
  public EntityExceptionEvent(EntityException e)
  {
    super(e.getEntity());
    
    this.e = e;
  }
  
  @Override
  public EntityException getException()
  {
    return e;
  }
  
}