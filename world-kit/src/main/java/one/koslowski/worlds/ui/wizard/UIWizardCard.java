package one.koslowski.worlds.ui.wizard;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import one.koslowski.wizard.api.WizardCard;
import one.koslowski.worlds.WorldKit;
import one.koslowski.worlds.WorldType;

class UIWizardCard extends Canvas implements PaintListener
{
  WizardCard card;
  
  Color highlightColor;
  
  boolean hidden;
  
  public UIWizardCard(Composite parent, WizardCard card)
  {
    super(parent, SWT.DOUBLE_BUFFERED);
    
    this.card = card;
  }
  
  @Override
  public Point computeSize(int wHint, int hHint, boolean changed)
  {
    Point size = new Point(wHint, hHint);
    if (size.x == SWT.DEFAULT)
      size.x = (int) (size.y * (2.0 / 3.0));
    if (size.y == SWT.DEFAULT)
      size.y = (int) (size.x * (3.0 / 2.0));
    return size;
  }
  
  @Override
  public void paintControl(PaintEvent e)
  {
    Image image = getImage();
    
    e.gc.setAntialias(SWT.ON);
    e.gc.setInterpolation(SWT.HIGH);
    
    e.gc.drawImage(image,
        // Quelle
        0, 0, image.getBounds().width, image.getBounds().height,
        // Ziel
        e.x, e.y, e.width, e.height);
        
    if (highlightColor != null)
    {
      e.gc.setLineWidth(e.height / 35);
      e.gc.setForeground(highlightColor);
      e.gc.drawRoundRectangle(e.x, e.y, e.width, e.height, e.width / 5, e.height / 5);
    }
  }
  
  String getShorthand()
  {
    if (card.isFool())
      return "N" + card.getNr();
    if (card.isWizard())
      return "Z" + card.getNr();
    return card.getColor().name().substring(0, 1) + card.getValue().toString();
  }
  
  private Image getImage()
  {
    return WorldKit.UI.getImage(WorldType.WIZARD, "CARD_" + (hidden ? "X0" : getShorthand()));
  }
  
  static Color getColor(WizardCard.Color color)
  {
    if (color == null)
      return null;
      
    switch (color)
    {
      case BLUE:
        return Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
      case GREEN:
        return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
      case RED:
        return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
      case YELLOW:
        return Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
      default:
        return null;
    }
  }
}