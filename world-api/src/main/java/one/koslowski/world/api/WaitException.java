package one.koslowski.world.api;

/**
 * Kann geworfen werden um auszudrücken, dass auf ein Entity gewartet werden muss.
 */
public class WaitException extends InterruptedException
{
  private static final long serialVersionUID = 1L;
  
  Entity entity;
  
  /**
   * @param object
   *          Objekt, auf das gewartet werden muss
   */
  public WaitException(Entity entity)
  {
    this.entity = entity;
  }
}