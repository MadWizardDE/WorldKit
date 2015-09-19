package one.koslowski.worlds.ui;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import one.koslowski.world.api.World;

public interface WorldController
{
  /**
   * @return muss stets dieselbe Welt zurückliefern
   */
  public World getWorld();
  
  /**
   * Ein Control zur Darstellung der Welt erzeugen.
   */
  public Control createContents(Composite parent, MenuManager menuBar);
  
  /**
   * Das Control zur Darstellung der Welt entsorgen.
   */
  public default void dispose(Control control, MenuManager menuBar)
  {
    control.dispose();
  }
}