package one.koslowski.worlds.network;

public class WorldBrowser
{
  private static ThreadLocal<WorldBrowser> DEFAULT = ThreadLocal.withInitial(() -> new WorldBrowser());
  
  public WorldBrowser()
  {
  
  }
  
  public static WorldBrowser getDefault()
  {
    return DEFAULT.get();
  }
  
}