package one.koslowski.worlds.host;

import java.io.Closeable;
import java.io.Serializable;
import java.util.List;

import one.koslowski.world.api.World;

public class WorldSession implements Closeable
{
  World world;

  private List<Link> links;

  WorldSession(World world)
  {
    this.world = world;
  }

  public SessionInfo getInfo()
  {
    return null;
  }

  public World getWorld()
  {
    return world;
  }

  @Override
  public void close()
  {

  }

  public static class SessionInfo implements Serializable
  {
    private static final long serialVersionUID = 1L;
  }
}