package one.koslowski.worlds.ui.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import com.google.common.eventbus.Subscribe;

import one.koslowski.wizard.api.WizardDeck;
import one.koslowski.wizard.api.events.CardDrawnEvent;
import one.koslowski.wizard.api.events.DeckShuffeledEvent;
import one.koslowski.worlds.WorldKit;
import one.koslowski.worlds.WorldType;

class UIWizardDeck extends Canvas implements PaintListener
{
  WizardDeck deck;

  public UIWizardDeck(Composite parent, WizardDeck deck)
  {
    super(parent, SWT.DOUBLE_BUFFERED);

    this.deck = deck;

    addPaintListener(this);
  }

  @Override
  public Point computeSize(int wHint, int hHint, boolean changed)
  {
    Point size = new Point(wHint, hHint);
    size.x = (int) (size.y * (2.0 / 3.0));
    return size;
  }

  @Override
  public void paintControl(PaintEvent e)
  {
    if (!deck.isEmpty())
    {
      Image image = getImage();
      {
        e.gc.setAntialias(SWT.ON);
        e.gc.setInterpolation(SWT.HIGH);
        e.gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height, e.x, e.y, e.width, e.height);
      }

      String text = Integer.toString(deck.getSize());
      {
        e.gc.setFont(WorldKit.UI.getFont(WorldType.WIZARD, "DECK_SIZE"));
        e.gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_WHITE));

        Point size = e.gc.textExtent(text);
        e.gc.drawString(text, (e.width - size.x) / 2, (e.height - size.y) / 2, true);
      }
    }
  }

  @Subscribe
  public void onShuffle(DeckShuffeledEvent event)
  {
    WorldKit.UI.async(this, null);
  }

  @Subscribe
  public void onCard(CardDrawnEvent event)
  {
    WorldKit.UI.async(this, null);
  }

  private Image getImage()
  {
    return WorldKit.UI.getImage(WorldType.WIZARD, "CARD_" + "X0");
  }
}