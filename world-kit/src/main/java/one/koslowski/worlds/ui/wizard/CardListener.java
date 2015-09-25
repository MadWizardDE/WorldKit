package one.koslowski.worlds.ui.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;

class CardListener implements PaintListener, MouseListener
{
  private Integer count;

  public CardListener()
  {
    this.count = null;
  }

  public CardListener(Integer count)
  {
    this.count = count;
  }

  @Override
  public void paintControl(PaintEvent e)
  {
    if (e.widget instanceof Composite)
    {
      Composite composite = (Composite) e.widget;

      Control[] children = composite.getChildren();

      if (children.length > 0)
      {
        int count = this.count != null ? this.count : children.length;

        Event event = new Event();
        event.display = e.display;
        event.time = e.time;
        event.data = e.data;
        event.gc = e.gc;

        for (int i = 0; i < children.length; i++)
        {
          UIWizardCard card = (UIWizardCard) children[i];
          event.widget = card;
          event.width = card.computeSize(SWT.DEFAULT, e.height).x;
          event.height = e.height;
          int spacing = count > 1 ? (e.width - event.width) / (count - 1) : 0;
          event.x = i * spacing;
          event.y = 0;

          card.paintControl(new PaintEvent(event));
        }
      }
    }
  }

  @Override
  public void mouseUp(MouseEvent e)
  {

  }

  @Override
  public void mouseDown(MouseEvent e)
  {

  }

  @Override
  public void mouseDoubleClick(MouseEvent e)
  {

  }

  public class UICardEvent
  {

  }
}