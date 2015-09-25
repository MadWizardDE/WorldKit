package one.koslowski.world.api;

import java.io.Serializable;

import org.apache.commons.math3.fraction.Fraction;

public class FrameDelimiter implements Serializable
{
  private static final long serialVersionUID = 1L;

  private volatile Fraction fps;

  private volatile transient long start, fpi;

  protected FrameDelimiter()
  {

  }

  public FrameDelimiter(double fps)
  {
    setFPS(fps);
  }

  public void setFPS(double fps)
  {
    this.fps = new Fraction(fps);

    start = fpi = 0;
  }

  public double getFPS()
  {
    return fps.doubleValue();
  }

  void beforePhase()
  {
    if (getRest() <= 0 || getMaxFrame() == fpi)
    {
      // neu anfangen zu zählen
      start = System.currentTimeMillis();
      fpi = 0;
    }
  }

  void afterPhase() throws ThrottleException
  {
    double rest = getRest() / (getMaxFrame() - fpi);

    fpi++;

    if (rest > 0)
    {
      throw new ThrottleException((long) rest);
    }
  }

  private int getInterval()
  {
    return fps.getDenominator() * 1000;
  }

  private int getMaxFrame()
  {
    return fps.getNumerator();
  }

  private double getRest()
  {
    return start + getInterval() - System.currentTimeMillis();
  }

  /**
   * Kann geworfen werden um auszudrücken, dass auf eine bestimmte Zeit gewartet werden muss.
   */
  static class ThrottleException extends RuntimeException
  {
    private static final long serialVersionUID = 1L;

    long time;

    /**
     * @param time
     *          Zeit in ms, die gewartet werden soll
     */
    ThrottleException(long time)
    {
      this.time = time;
    }
  }
}