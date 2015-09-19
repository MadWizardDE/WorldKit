package one.koslowski.worlds.network;

public class WorldHost
{
  private static ThreadLocal<WorldHost> DEFAULT = ThreadLocal.withInitial(() -> new WorldHost());
  
  public WorldHost()
  {
  
  }
  
  public static WorldHost getDefault()
  {
    return DEFAULT.get();
  }
  
}