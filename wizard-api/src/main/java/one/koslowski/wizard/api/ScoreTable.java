package one.koslowski.wizard.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ScoreTable implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  private List<WizardPlayer> players;
  
  private List<Map<WizardPlayer, Round>> rounds;
  
  ScoreTable(List<WizardPlayer> players)
  {
    this.players = players;
    
    this.rounds = new ArrayList<>(getMaxRoundCount());
  }
  
  public int getPlayerCount()
  {
    return players.size();
  }
  
  public WizardPlayer getPlayer(int nr)
  {
    return players.get(nr - 1);
  }
  
  public int getMaxRoundCount()
  {
    switch (getPlayerCount())
    {
      case 3:
        return 20;
      case 4:
        return 15;
      case 5:
        return 12;
      case 6:
        return 10;
        
      default:
        throw new IllegalStateException();
    }
  }
  
  public int getRoundCount()
  {
    return rounds.size();
  }
  
  public Map<WizardPlayer, Round> getRound(int nr)
  {
    if (nr < 1 || nr > getRoundCount())
      return null;
      
    return rounds.get(nr - 1);
  }
  
  Map<WizardPlayer, Round> getRound()
  {
    return getRound(getRoundCount());
  }
  
  private Map<WizardPlayer, Round> getLastRound()
  {
    return getRound(getRoundCount() - 1);
  }
  
  void predict(WizardPlayer player, int tricks)
  {
    if (tricks < 0)
      throw new IllegalArgumentException();
      
    assert players.contains(player);
    
    Round round = getRound().get(player);
    
    assert round == null;
    
    getRound().put(player, new Round(tricks));
  }
  
  public Integer getPredictedTricks(WizardPlayer player)
  {
    if (getRound() == null)
      return null;
      
    Round score = getRound().get(player);
    
    return score == null ? null : score.tricks;
  }
  
  void count(WizardPlayer player, int tricks)
  {
    if (tricks < 0)
      throw new IllegalArgumentException();
      
    assert players.contains(player);
    
    Round round = getRound().get(player);
    
    assert round != null && round.score == null;
    
    int s = 0;
    if (getRoundCount() > 1)
      s = getLastRound().get(player).score;
      
    if (round.tricks == tricks)
    {
      round.score = s + (20 + 10 * tricks);
      round.win = true;
    }
    else
    {
      round.score = s - (10 * Math.abs(round.tricks - tricks));
      round.win = false;
    }
  }
  
  public int getScore(WizardPlayer player)
  {
    if (getRound() == null)
      return 0;
      
    Round round = getRound().get(player);
    
    if (round == null || round.score == null)
    {
      if (getRoundCount() == 1)
        return 0;
        
      return getLastRound().get(player).score;
    }
    
    return round.score;
  }
  
  void next()
  {
    rounds.add(new HashMap<>(getPlayerCount()));
  }
  
  public class Round implements Serializable
  {
    private static final long serialVersionUID = 1L;
    
    private Integer score;
    private Integer tricks;
    private Boolean win;
    
    private Round(int predictedTricks)
    {
      tricks = predictedTricks;
    }
    
    public Integer getScore()
    {
      return score;
    }
    
    public Integer getPredictedTricks()
    {
      return tricks;
    }
    
    public Boolean isWin()
    {
      return win;
    }
  }
}