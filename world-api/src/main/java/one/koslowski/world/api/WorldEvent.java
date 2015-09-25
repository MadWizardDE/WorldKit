package one.koslowski.world.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.EventObject;

/**
 * Ein Event, dass von oder innerhalb einer Welt ausgelöst wird.
 */
public class WorldEvent extends EventObject
{
  private static final long serialVersionUID = 1L;

  World world;

  public WorldEvent(Object source)
  {
    super(source);
  }

  /**
   * @return die Welt, in der das Event stattgefunden hat
   */
  public World getWorld()
  {
    return world;
  }

  @Inherited
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface EventHandler
  {

  }
}