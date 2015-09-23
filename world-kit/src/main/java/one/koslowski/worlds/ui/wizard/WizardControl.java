package one.koslowski.worlds.ui.wizard;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import one.koslowski.wizard.api.WizardCard;
import one.koslowski.wizard.api.WizardPlayer;
import one.koslowski.wizard.api.WizardWorld;
import one.koslowski.wizard.api.WizardWorld.WizardContext;
import one.koslowski.wizard.api.events.CardDrawnEvent;
import one.koslowski.wizard.api.events.DeckShuffeledEvent;
import one.koslowski.wizard.api.events.TrumpColorEvent;
import one.koslowski.world.api.EventListener;
import one.koslowski.worlds.WorldKit;
import one.koslowski.worlds.WorldType;

class WizardControl extends Composite implements EventListener
{
  static
  {
    ImageRegistry images = WorldKit.UI.getImageRegistry(WorldType.WIZARD);
    {
      // Deckblatt
      loadCardImage(images, "X0");
      
      // Farb-Karten
      for (WizardCard.Color color : WizardCard.Color.values())
        for (int value = WizardCard.LOW_VALUE; value <= WizardCard.HIGH_VALUE; value++)
          loadCardImage(images, color.name().substring(0, 1) + value);
          
      // Sonderkarten
      for (int nr = 1; nr <= 4; nr++)
      {
        loadCardImage(images, "N" + nr);
        loadCardImage(images, "Z" + nr);
      }
      
      // Dealer
      images.put("DEALER", ImageDescriptor.createFromFile(WizardControl.class, "dealer.png"));
    }
    
    FontRegistry fonts = WorldKit.UI.getFontRegistry(WorldType.WIZARD);
    {
      // Anzahl Stapel-Karten
      fonts.put("DECK_SIZE", fonts.defaultFontDescriptor().setHeight(20).getFontData());
    }
  }
  
  private static void loadCardImage(ImageRegistry registry, String name)
  {
    registry.put("CARD_" + name, ImageDescriptor.createFromFile(WizardControl.class, "cards/" + name + ".png"));
  }
  
  EventBus eventBus;
  
  // +++ UI +++ //
  private Composite            cards;
  private UIWizardDeck         deck;
  private UIWizardCard         trumpCard;
  private List<UIWizardPlayer> players;
  
  UIScoreTable scoreWindow;
  
  {
    players = new ArrayList<>();
  }
  
  public WizardControl(Composite parent, MenuManager menu, WizardWorld world)
  {
    super(parent, SWT.DOUBLE_BUFFERED);
    
    eventBus = new EventBus();
    eventBus.register(this);
    
    WizardContext context = WizardWorld.getContext();
    
    cards = new Composite(this, SWT.DOUBLE_BUFFERED);
    {
      cards.setLayout(new CardsLayout());
      
      // Kartenstapel
      deck = new UIWizardDeck(cards, context.getDeck());
      eventBus.register(deck);
      
      // Trumpf-Karte
      if (context.getTrumpCard() != null)
      {
        trumpCard = new UIWizardCard(cards, context.getTrumpCard());
        trumpCard.highlightColor = UIWizardCard.getColor(context.getTrumpColor());
        trumpCard.addPaintListener(trumpCard);
      }
    }
    
    for (WizardPlayer player : context.getPlayers())
    {
      // Spieler (inkl. Stich- und Handkarten)
      UIWizardPlayer ui = new UIWizardPlayer(this, player, context);
      eventBus.register(ui);
      players.add(ui);
    }
    
    FillLayout layout = new FillLayout(SWT.VERTICAL);
    layout.spacing = 5;
    layout.marginHeight = 5;
    layout.marginWidth = 10;
    this.setLayout(layout);
    
    layout();
    redraw();
  }
  
  @Override
  public void processEvent(EventObject event)
  {
    eventBus.post(event);
  }
  
  @Subscribe
  public void onShuffle(DeckShuffeledEvent event)
  {
    if (trumpCard != null)
    {
      WorldKit.UI.async(cards, () ->
      {
        trumpCard.dispose();
        trumpCard = null;
      });
    }
  }
  
  @Subscribe
  public void onDraw(CardDrawnEvent event)
  {
    if (event.getTarget() == null)
    {
      WorldKit.UI.async(cards, () ->
      {
        trumpCard = new UIWizardCard(cards, event.getCard());
        trumpCard.addPaintListener(trumpCard);
        cards.layout();
      });
    }
  }
  
  @Subscribe
  public void onTrumpColor(TrumpColorEvent event)
  {
    WizardCard card = WizardWorld.getContext().getTrumpCard();
    
    if (card != null)
    {
      WorldKit.UI.async(cards, () ->
      {
        trumpCard.highlightColor = UIWizardCard.getColor(event.getColor());
        trumpCard.redraw();
      });
    }
  }
  
  private class CardsLayout extends Layout
  {
    @Override
    protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache)
    {
      return new Point(wHint, hHint);
    }
    
    @Override
    protected void layout(Composite composite, boolean flushCache)
    {
      if (composite == cards)
      {
        Point size = deck.computeSize(SWT.DEFAULT, cards.getSize().y);
        
        deck.setLocation(0, 0);
        deck.setSize(size);
        
        if (trumpCard != null)
        {
          trumpCard.setLocation(size.x + 10, 0);
          trumpCard.setSize(size);
        }
      }
    }
  }
}