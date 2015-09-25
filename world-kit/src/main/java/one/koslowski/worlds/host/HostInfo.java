package one.koslowski.worlds.host;

import java.io.Serializable;
import java.util.UUID;

public class HostInfo implements Serializable
{
  private static final long serialVersionUID = 1L;

  private UUID id;

  private String name;

  private HostType type;

  public HostInfo()
  {

  }

  public enum HostType
  {
    HEADLESS, GUI;
  }
}