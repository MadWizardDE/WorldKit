package one.koslowski.worlds.network;

import one.koslowski.world.api.Entity;

public abstract class EntityNetworkAdapter<E extends Entity>
{
  
  public EntityNetworkAdapter(E entity)
  {
  
  }
  
  protected abstract void send(E entity, Object data);
  
  protected abstract void receive(E entity, Object data);
}
