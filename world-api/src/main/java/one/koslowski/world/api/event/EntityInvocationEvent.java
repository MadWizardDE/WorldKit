package one.koslowski.world.api.event;

import one.koslowski.world.api.EntityEvent;
import one.koslowski.world.api.EntityInvocation;

public class EntityInvocationEvent extends EntityEvent implements SystemEvent
{
  private static final long serialVersionUID = 1L;
  
  private EntityInvocation invocation;
  
  public EntityInvocationEvent(EntityInvocation invocation)
  {
    super(invocation.getEntity());
    
    this.invocation = invocation;
  }
  
  public EntityInvocation getInvocation()
  {
    return invocation;
  }
}