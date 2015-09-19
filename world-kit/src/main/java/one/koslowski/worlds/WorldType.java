package one.koslowski.worlds;

import one.koslowski.connect4.api.Connect4World;
import one.koslowski.wizard.api.WizardWorld;
import one.koslowski.world.api.World;

public enum WorldType
{
  WIZARD("Wizard"),
  
  CONNECT4("Vier gewinnt"),
  
  CONWAYS("Conways Spiel des Lebens");
  
  private String name;
  
  private WorldType(String name)
  {
    this.name = name;
  }
  
  public String getName()
  {
    return name;
  }
  
  public static WorldType of(World world)
  {
    if (world instanceof Connect4World)
      return CONNECT4;
    else if (world instanceof WizardWorld)
      return WIZARD;
    else
      throw new UnsupportedOperationException();
  }
}