package one.koslowski.worlds.network;

import java.util.LinkedHashMap;
import java.util.Map;

import one.koslowski.world.api.World;
import one.koslowski.world.api.WorldEvent;
import one.koslowski.world.api.WorldEventListener;
import one.koslowski.world.api.WorldManager;
import one.koslowski.worlds.WorldKit;

public class WorldHost implements WorldEventListener
{
  private WorldManager manager;
  
  private Map<World, WorldSession> sessions;
  
  {
    sessions = new LinkedHashMap<>();
  }
  
  public WorldHost(WorldManager manager)
  {
    this.manager = manager;
    this.manager.addListener(this);
  }
  
  public static WorldHost getDefault()
  {
    return WorldKit.HOST;
  }
  
  @Override
  public void processEvent(WorldEvent event)
  {
  
  }
  
  public WorldSession getSession(World world)
  {
    return sessions.get(world);
  }
}