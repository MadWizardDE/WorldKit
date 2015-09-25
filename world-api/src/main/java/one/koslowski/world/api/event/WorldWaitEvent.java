package one.koslowski.world.api.event;

import one.koslowski.world.api.Entity;
import one.koslowski.world.api.World;

public class WorldWaitEvent extends WorldStateEvent
{
  private static final long serialVersionUID = 1L;

  private Entity enttiy;

  public boolean wait = true;

  public WorldWaitEvent(World source, Entity entity)
  {
    super(source);

    this.enttiy = entity;
  }

  public Entity getEntity()
  {
    return enttiy;
  }
}