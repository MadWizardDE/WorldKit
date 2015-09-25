package one.koslowski.worlds.ui.wizard;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import com.google.common.eventbus.Subscribe;

import one.koslowski.wizard.api.WizardCard;
import one.koslowski.wizard.api.WizardPlayer;
import one.koslowski.wizard.api.WizardTrick;
import one.koslowski.wizard.api.WizardWorld;
import one.koslowski.wizard.api.WizardWorld.WizardContext;
import one.koslowski.wizard.api.events.CardEvent;
import one.koslowski.wizard.api.events.DeckShuffeledEvent;
import one.koslowski.wizard.api.events.TrickPlayedEvent;
import one.koslowski.wizard.api.events.TrickStartedEvent;
import one.koslowski.wizard.api.events.TricksPredictedEvent;
import one.koslowski.worlds.WorldKit;
import one.koslowski.worlds.WorldType;

class UIWizardPlayer extends Composite
{
  // +++ API +++ //
  WizardWorld  world;
  WizardPlayer player;
  // ScoreTable scores;

  private boolean isDealer;

  // +++ UI +++ //
  private CLabel    tricks;
  private Group     group;
  private Composite cards;
  private Composite trick;

  public UIWizardPlayer(Composite parent, WizardPlayer player, WizardContext context)
  {
    super(parent, SWT.DOUBLE_BUFFERED);

    this.player = player;

    this.world = context.getWorld();
    // this.scores = context.getScoreTable();
    this.isDealer = context.getDealer() == player;

    tricks = new CLabel(this, SWT.NONE);
    tricks.setAlignment(SWT.CENTER);
    tricks.setLayoutData(new GridData(75, SWT.DEFAULT));

    group = new Group(this, SWT.NONE);
    group.setText(player.getName());
    group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    FillLayout layout = new FillLayout();
    layout.marginHeight = layout.marginWidth = 5;
    group.setLayout(layout);
    cards = new Composite(group, SWT.DOUBLE_BUFFERED);
    cards.addPaintListener(new CardListener(context.getScoreTable().getMaxRoundCount()));

    // TODO Layout
    trick = new Composite(this, SWT.DOUBLE_BUFFERED);
    GridData data = new GridData(150, SWT.DEFAULT);
    data.verticalAlignment = SWT.FILL;
    trick.setLayoutData(data);
    trick.addPaintListener(new CardListener());
    trick.addMouseListener(new CardListener());

    this.setLayout(new GridLayout(3, false));

    for (WizardCard card : player.getCards())
      addCard(card);

    WizardTrick trick = context.getTrick();
    if (trick != null)
      if (trick.getCards().size() == context.getPlayers().size())
      {
        if (trick.getPlayer() == player)
          for (WizardCard card : trick.getCards())
            addTrickCard(card);
      }
      else
        addTrickCard(trick.getCard(player));

    updateTricks(getPredictedTricks());
  }

  @Subscribe
  public void onShuffle(DeckShuffeledEvent event)
  {
    isDealer = WizardWorld.getContext().getDealer() == player;

    Pair<Integer, Integer> tuple = getPredictedTricks();

    WorldKit.UI.async(this, () -> cleanup(tuple));
  }

  @Subscribe
  public void onPrediction(TricksPredictedEvent event)
  {
    if (event.getSource() == player)
    {
      Pair<Integer, Integer> tuple = getPredictedTricks();

      WorldKit.UI.async(tricks, () -> updateTricks(tuple));
    }
  }

  @Subscribe
  public void onTrickStarted(TrickStartedEvent event)
  {
    Pair<Integer, Integer> tuple = getPredictedTricks();

    WorldKit.UI.async(this, () -> cleanup(tuple));
  }

  @Subscribe
  public void onCard(CardEvent event)
  {
    WizardCard card = event.getCard();

    if (event.getTarget() == player)
      WorldKit.UI.async(cards, () -> addCard(card));
    else if (event.getSource() == player)
    {
      WorldKit.UI.async(cards, () ->
      {
        UIWizardCard ui = findUICard(card);
        ui.setParent(trick);
        ui.hidden = false;

        trick.redraw();
      });
    }
  }

  @Subscribe
  public void onTrickPlayed(TrickPlayedEvent event)
  {
    List<WizardCard> cards = new ArrayList<>();

    if (event.getTrick().getPlayer() == player)
      cards.addAll(event.getTrick().getCards());

    Pair<Integer, Integer> tuple = getPredictedTricks();

    WorldKit.UI.async(trick, () ->
    {
      removeTrickCards();

      if (!cards.isEmpty())
      {
        for (WizardCard card : cards)
          addTrickCard(card);

        updateTricks(tuple);

        layout();
      }
    });
  }

  void addCard(WizardCard card)
  {
    UIWizardCard ui = new UIWizardCard(cards, card);
    ui.hidden = true;
    cards.redraw();
    layout();
  }

  UIWizardCard findUICard(WizardCard card)
  {
    for (Control control : cards.getChildren())
      if (control instanceof UIWizardCard)
        if (((UIWizardCard) control).card == card)
          return (UIWizardCard) control;

    return null;
  }

  void removeCard(WizardCard card)
  {
    for (Control control : cards.getChildren())
      if (control instanceof UIWizardCard)
        if (((UIWizardCard) control).card == card)
          control.dispose();

    layout();
  }

  void addTrickCard(WizardCard card)
  {
    if (card != null)
    {
      new UIWizardCard(trick, card);
    }
  }

  void removeTrickCards()
  {
    for (Control child : trick.getChildren())
      child.dispose();
  }

  void updateTricks(Pair<Integer, Integer> tuple)
  {
    String text = "";

    text += tuple.getLeft();

    if (tuple.getRight() != null)
    {
      text += " / " + tuple.getRight();
    }

    tricks.setText(text);
    tricks.setImage(isDealer ? WorldKit.UI.getImage(WorldType.WIZARD, "DEALER") : null);

    tricks.redraw();
    layout();
  }

  void cleanup(Pair<Integer, Integer> tuple)
  {
    removeTrickCards();
    updateTricks(tuple);

    trick.redraw();
  }

  private Pair<Integer, Integer> getPredictedTricks()
  {
    Integer predicted = WizardWorld.getContext().getScoreTable().getPredictedTricks(player);

    return Pair.of(player.getTricks().size(), predicted);
  }
}