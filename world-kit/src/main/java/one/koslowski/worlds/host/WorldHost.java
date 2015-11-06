package one.koslowski.worlds.host;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import one.koslowski.world.api.World;
import one.koslowski.world.api.WorldEvent;
import one.koslowski.world.api.WorldEventListener;
import one.koslowski.world.api.WorldManager;
import one.koslowski.world.api.event.WorldAddedEvent;
import one.koslowski.world.api.event.WorldRemovedEvent;
import one.koslowski.worlds.host.Link.LinkDescriptor;

public class WorldHost
{
  private WorldManager manager;

  private Server  server;
  private Browser browser;

  private List<Link> links;

  private Map<World, WorldSession> sessions;

  {
    links = new LinkedList<>();
    sessions = new LinkedHashMap<>();
  }

  public WorldHost(WorldManager manager)
  {
    this.manager = manager;
    this.manager.addListener(new WorldListener());

    server = new Server();
    browser = new Browser();
  }

  public Server getServer()
  {
    return server;
  }

  public Browser getBrowser()
  {
    return browser;
  }

  public WorldSession getSession(World world)
  {
    return sessions.get(world);
  }

  public <T extends Link> T connect(LinkDescriptor<T> descriptor)
  {
    return null;
  }

  private class WorldListener implements WorldEventListener
  {
    @Override
    public void processEvent(WorldEvent event)
    {
      if (event instanceof WorldAddedEvent)
        add((WorldAddedEvent) event);
      else if (event instanceof WorldRemovedEvent)
        remove((WorldRemovedEvent) event);
    }

    private void add(WorldAddedEvent event)
    {
      if (!sessions.containsKey(event.getWorld()))
      {
        WorldSession session = new WorldSession(event.getWorld());

        session.addHost(WorldHost.this);

        sessions.put(event.getWorld(), session);
      }
    }

    private void remove(WorldRemovedEvent event)
    {
      WorldSession session = sessions.remove(event.getWorld());

      session.close();
    }
  }
}