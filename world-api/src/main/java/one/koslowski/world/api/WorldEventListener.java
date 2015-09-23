package one.koslowski.world.api;

import java.util.EventListener;

public interface WorldEventListener extends EventListener
{
  public void processEvent(WorldEvent event);
}