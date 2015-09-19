package one.koslowski.world.api;

public abstract class EntityInvocationHandler
{
  
  protected abstract Object dispatch(EntityInvocation invocation) throws Throwable;
  
}