package one.koslowski.worlds.host;

import java.io.Closeable;
import java.io.Serializable;
import java.util.List;

import one.koslowski.world.api.World;
import one.koslowski.worlds.WorldType;
import one.koslowski.worlds.host.message.SyncMessage;
import one.koslowski.worlds.host.message.SyncMessage.SyncChange;

public class WorldSession implements Closeable
{
  World world;

  private List<Host> hosts;

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

  public void sync(SyncChange change)
  {
    postMessage(new SyncMessage(change));
  }

  public void postMessage(Message message)
  {
    for (Host host : hosts)
      host.sendMessage(message);
  }

  @Override
  public void close()
  {

  }

  public static class SessionInfo implements Serializable
  {
    private static final long serialVersionUID = 1L;

    private WorldType type;

    public WorldType getType()
    {
      return type;
    }
  }
}