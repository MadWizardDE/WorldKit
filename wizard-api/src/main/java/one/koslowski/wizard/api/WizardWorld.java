package one.koslowski.wizard.api;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import one.koslowski.wizard.api.WizardPlayer.WizardPlayerStrategy;
import one.koslowski.wizard.api.events.CardDrawnEvent;
import one.koslowski.wizard.api.events.CardPlayedEvent;
import one.koslowski.wizard.api.events.GameOverEvent;
import one.koslowski.wizard.api.events.GameStartedEvent;
import one.koslowski.wizard.api.events.PlayerCheatedEvent;
import one.koslowski.wizard.api.events.TrickPlayedEvent;
import one.koslowski.wizard.api.events.TrickStartedEvent;
import one.koslowski.wizard.api.events.TricksPredictedEvent;
import one.koslowski.wizard.api.events.TrumpColorEvent;
import one.koslowski.world.api.Entity;
import one.koslowski.world.api.World;

public class WizardWorld extends World
{
  private static final long serialVersionUID = 1L;
  
  public static final int MIN_PLAYERS = 3, MAX_PLAYERS = 6;
  
  private List<WizardPlayer> players;
  
  private ScoreTable scoreTable;
  
  private WizardPlayer dealer;
  private WizardDeck   deck;
  
  private WizardCard.Color trumpColor;
  private WizardCard       trumpCard;
  
  private WizardTrick trick;
  
  {
    players = new CopyOnWriteArrayList<>();
  }
  
  public WizardWorld()
  {
    addContext(new WizardContext());
    
    this.phase = () ->
    {
      if (deck == null)
        deck = new WizardDeck();
      if (deck.getSize() != WizardDeck.SIZE)
        throw new IllegalArgumentException("deck.size != " + WizardDeck.SIZE + ")");
        
      if (players.size() < MIN_PLAYERS)
        throw new IllegalStateException("players < " + MIN_PLAYERS);
        
      // "Block der Wahrheit"
      scoreTable = new ScoreTable(players);
      
      publishEvent(new GameStartedEvent(this));
      
      // wenn kein Kartengeber bestimmt wurde, gibt der 1. Spieler
      return () -> shuffle(dealer != null ? dealer : players.get(0));
    };
  }
  
  public static WizardContext getContext()
  {
    return getContext(WizardContext.class);
  }
  
  public WizardDeck getDeck()
  {
    return deck;
  }
  
  public List<WizardPlayer> getPlayers()
  {
    return players;
  }
  
  /**
   * (weiteren) Spieler hinzufügen.
   */
  public void addPlayer(WizardPlayer player)
  {
    addEntity(player);
  }
  
  /**
   * Karten-Geber bestimmen.
   */
  public void setDealer(WizardPlayer player)
  {
    if (!players.contains(player))
      throw new IllegalArgumentException();
      
    dealer = player;
  }
  
  /**
   * @param player
   *          relativ zu diesem Spieler
   * @param left
   *          soviele Plätze weiter links
   */
  private WizardPlayer getPlayer(WizardPlayer player, int left)
  {
    return players.get((players.indexOf(player) + left) % players.size());
  }
  
  Phase shuffle(WizardPlayer dealer)
  {
    this.dealer = dealer;
    
    scoreTable.next();
    
    // Karten mischen
    deck.shuffle();
    
    return this::dealCards;
  }
  
  Phase dealCards()
  {
    // Karten verteilen (eine pro Runde pro Spieler)
    for (int i = 0; i < scoreTable.getRoundCount(); i++)
      for (WizardPlayer player : players)
      {
        WizardCard card = deck.draw();
        
        player.getCards().add(card);
        
        publishEvent(new CardDrawnEvent(deck, player, card));
      }
      
    // Trumpf-Karte aufdecken
    if (!deck.isEmpty())
    {
      trumpCard = deck.draw();
      
      publishEvent(new CardDrawnEvent(deck, null, trumpCard));
    }
    
    return this::determineTrumpColor;
  }
  
  Phase determineTrumpColor() throws InterruptedException
  {
    // Trumpf-Farbe bestimmen
    WizardPlayer player = null;
    
    if (trumpCard != null)
    {
      if (trumpCard.isWizard())
        trumpColor = ((WizardPlayerStrategy) (player = dealer).x).defineTrumpColor();
      else
        trumpColor = trumpCard.getColor();
    }
    
    publishEvent(new TrumpColorEvent(player != null ? player : this, trumpColor));
    
    return () -> predictTricks(getPlayer(dealer, 1));
  }
  
  Phase predictTricks(WizardPlayer player) throws InterruptedException
  {
    // Stiche vorhersagen
    int tricks = ((WizardPlayerStrategy) player.x).predictTricks();
    
    try
    {
      scoreTable.predict(player, tricks);
    }
    catch (IllegalArgumentException e)
    {
      return () -> predictTricks(player);
    }
    
    publishEvent(new TricksPredictedEvent(player, tricks));
    
    // haben alle ihre Stiche vorhergesagt?
    if (scoreTable.getRound().size() == players.size())
      // dann fängt der Spieler links vom Dealer an
      return () -> beginTrick(getPlayer(dealer, 1));
      
    return () -> predictTricks(getPlayer(player, 1));
  }
  
  Phase beginTrick(WizardPlayer player)
  {
    trick = new WizardTrick(trumpColor);
    
    publishEvent(new TrickStartedEvent(trick));
    
    return () -> playCard(player);
  }
  
  Phase playCard(WizardPlayer player) throws InterruptedException
  {
    // Karte spielen
    WizardCard card = ((WizardPlayerStrategy) player.x).playCard();
    
    try
    {
      trick.play(player, card);
      
      player.getCards().remove(card);
    }
    catch (IllegalMoveException e)
    {
      publishEvent(new PlayerCheatedEvent(player, trick, card));
      
      return () -> this.playCard(player);
    }
    
    publishEvent(new CardPlayedEvent(player, trick, card));
    
    // Stich auswerten?
    if (trick.getCards().size() == players.size())
      return this::endTrick;
      
    return () -> playCard(getPlayer(player, 1));
  }
  
  Phase endTrick()
  {
    WizardPlayer player = trick.getPlayer();
    
    // Spieler erhält den Stich ...
    player.getTricks().add(trick);
    
    publishEvent(new TrickPlayedEvent(trick));
    
    trick = null;
    
    // alle Karten gespielt?
    if (player.getCards().isEmpty())
      return this::countTricks;
      
    // ... und fängt die nächste Runde an
    return () -> beginTrick(player);
  }
  
  Phase countTricks()
  {
    for (WizardPlayer p : players)
    {
      // Auswertung
      scoreTable.count(p, p.getTricks().size());
      
      // Karten einsammeln
      for (WizardTrick trick : p.getTricks())
        deck.collect(trick.getCards());
        
      // Stiche entfernen
      p.getTricks().forEach(trick -> removeEntity(trick));
      p.getTricks().clear();
    }
    
    // ggfs. auch die Trumpf-Karte
    deck.collect(trumpCard);
    trumpColor = null;
    trumpCard = null;
    
    // ist es wirklich schon so spät?
    if (scoreTable.getRoundCount() == scoreTable.getMaxRoundCount())
      return this::scoring;
      
    // der Spieler links vom Dealer gibt nächste Runde
    return () -> shuffle(getPlayer(dealer, 1));
  }
  
  Phase scoring()
  {
    // wer hat gewonnen? TODO Gleichstand?
    WizardPlayer winner = null;
    for (WizardPlayer player : players)
      if (winner == null || scoreTable.getScore(player) > scoreTable.getScore(winner))
        winner = player;
        
    publishEvent(new GameOverEvent(this, winner));
    
    return null; // halt
  }
  
  @Override
  protected void addEntity(Entity entity)
  {
    if (entity instanceof WizardDeck)
      if (getEntityContext().count(WizardDeck.class) > 0)
        throw new IllegalStateException();
        
    if (entity instanceof WizardPlayer)
    {
      if (getEntityContext().count(WizardPlayer.class) == MAX_PLAYERS || getState() != WorldState.FRESH)
        throw new IllegalStateException();
        
      players.add((WizardPlayer) entity);
    }
    
    super.addEntity(entity);
  }
  
  public class WizardContext extends WorldContext<WizardWorld>
  {
    private static final long serialVersionUID = 1L;
    
    public ScoreTable getScoreTable()
    {
      return scoreTable;
    }
    
    public List<WizardPlayer> getPlayers()
    {
      return players;
    }
    
    public WizardPlayer getPlayer(WizardPlayer player, int left)
    {
      return WizardWorld.this.getPlayer(player, left);
    }
    
    public WizardPlayer getDealer()
    {
      return dealer;
    }
    
    public WizardDeck getDeck()
    {
      return deck;
    }
    
    public WizardCard getTrumpCard()
    {
      return trumpCard;
    }
    
    public WizardCard.Color getTrumpColor()
    {
      return trumpColor;
    }
    
    public WizardTrick getTrick()
    {
      return trick;
    }
  }
}