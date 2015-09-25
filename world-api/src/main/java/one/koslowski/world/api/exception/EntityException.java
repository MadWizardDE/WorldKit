package one.koslowski.world.api.exception;

import one.koslowski.world.api.Entity;

public abstract class EntityException extends Exception
{
  private static final long serialVersionUID = 1L;

  private Entity entity;

  public EntityException(Entity entity, Throwable t)
  {
    super(t);

    this.entity = entity;
  }

  public Entity getEntity()
  {
    return entity;
  }

}