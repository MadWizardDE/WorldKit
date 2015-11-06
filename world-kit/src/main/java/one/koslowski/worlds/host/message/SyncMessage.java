package one.koslowski.worlds.host.message;

import java.io.Serializable;

import one.koslowski.world.api.World;

public class SyncMessage extends SessionMessage
{
  private static final long serialVersionUID = 1L;

  private SyncChange change;

  public SyncMessage(SyncChange change)
  {
    this.change = change;
  }

  public SyncChange getChange()
  {
    return change;
  }

  public interface SyncChange extends Serializable
  {
    void change(World world);
  }
}