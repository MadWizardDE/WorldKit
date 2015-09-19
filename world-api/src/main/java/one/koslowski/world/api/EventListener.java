package one.koslowski.world.api;

import java.util.EventObject;

public interface EventListener extends java.util.EventListener
{
  public void processEvent(EventObject event);
}