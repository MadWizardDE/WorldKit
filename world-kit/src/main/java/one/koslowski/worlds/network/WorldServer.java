package one.koslowski.worlds.network;

public class WorldServer
{
  private static ThreadLocal<WorldServer> DEFAULT = ThreadLocal.withInitial(() -> new WorldServer());
  
  public WorldServer()
  {
  
  }
  
  public static WorldServer getDefault()
  {
    return DEFAULT.get();
  }
  
}