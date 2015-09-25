package one.koslowski.world.api;

public class EntityEvent extends WorldEvent
{
  private static final long serialVersionUID = 1L;

  protected EntityEvent(Entity source)
  {
    super(source);
  }

  @Override
  public Entity getSource()
  {
    return (Entity) source;
  }

}