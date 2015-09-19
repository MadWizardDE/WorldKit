package one.koslowski.world.api.exception;

import one.koslowski.world.api.EntityInvocation;

public class EntityInvocationException extends EntityException
{
  private static final long serialVersionUID = 1L;
  
  private EntityInvocation invocation;
  
  public EntityInvocationException(EntityInvocation invocation, Throwable t)
  {
    super(invocation.getEntity(), t);
    
    this.invocation = invocation;
  }
  
  public EntityInvocation getInvocation()
  {
    return invocation;
  }
  
}