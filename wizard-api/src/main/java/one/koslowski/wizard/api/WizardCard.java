package one.koslowski.wizard.api;

import one.koslowski.world.api.Entity;

public class WizardCard extends Entity
{
  private static final long serialVersionUID = 1L;
  
  /** niedrigste und höchste farbige Karte */
  public static final int LOW_VALUE = 1, HIGH_VALUE = 13;
  /** niedrigste und höchste farblose Karte (Zauberer und Narr) */
  public static final int MIN_VALUE = LOW_VALUE - 1, MAX_VALUE = HIGH_VALUE + 1;
  
  private Color   color;
  private Integer value;
  
  private Integer nr;
  
  /**
   * Farbkarte erzeugen.
   */
  WizardCard(Color color, int value)
  {
    if (color == null || (value < LOW_VALUE || value > HIGH_VALUE))
      throw new IllegalArgumentException();
      
    this.color = color;
    this.value = value;
  }
  
  /**
   * Sonderkarte erzeugen.
   */
  WizardCard(int value, int nr)
  {
    if (color != null || (value != MIN_VALUE && value != MAX_VALUE))
      throw new IllegalArgumentException();
      
    this.value = value;
    this.nr = nr;
  }
  
  public Color getColor()
  {
    return color;
  }
  
  public Integer getValue()
  {
    return value;
  }
  
  public Integer getNr()
  {
    return nr;
  }
  
  public boolean isFool()
  {
    return value == MIN_VALUE;
  }
  
  public boolean isWizard()
  {
    return value == MAX_VALUE;
  }
  
  @Override
  public String toString()
  {
    return "WizardCard[" + color + "-" + value + (nr != null ? "#" + nr : "") + "]";
  }
  
  public enum Color
  {
    RED, BLUE, GREEN, YELLOW
  }
}