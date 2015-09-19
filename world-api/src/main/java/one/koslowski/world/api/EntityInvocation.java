package one.koslowski.world.api;

import java.lang.reflect.Method;

public class EntityInvocation
{
  Entity entity;
  
  Method   method;
  Object[] args;
  
  Object    result;
  Throwable exception;
  
  public EntityInvocation(Entity entity, Method method, Object[] args)
  {
    this.entity = entity;
    
    this.method = method;
    this.args = args;
  }
  
  public Entity getEntity()
  {
    return entity;
  }
  
  public Method getMethod()
  {
    return method;
  }
  
  public Object[] getParameters()
  {
    return args;
  }
  
  public Object getReturn()
  {
    return result;
  }
  
  public Throwable getException()
  {
    return exception;
  }
}