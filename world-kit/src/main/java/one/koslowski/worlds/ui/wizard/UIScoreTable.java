package one.koslowski.worlds.ui.wizard;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

import com.google.common.eventbus.Subscribe;

import one.koslowski.wizard.api.ScoreTable;
import one.koslowski.wizard.api.ScoreTable.Round;
import one.koslowski.wizard.api.WizardPlayer;
import one.koslowski.wizard.api.events.DeckShuffeledEvent;
import one.koslowski.wizard.api.events.GameOverEvent;
import one.koslowski.worlds.WorldKit;

class UIScoreTable extends Window
{
  private ScoreTable scores;
  
  private TableViewer tableViewer;
  
  public UIScoreTable(Shell parent, ScoreTable scores)
  {
    super(parent);
    
    this.scores = scores;
    
    setShellStyle(SWT.CLOSE | SWT.TITLE | SWT.TOOL);
  }
  
  @Override
  protected void configureShell(Shell shell)
  {
    super.configureShell(shell);
    
    shell.setText("Der Block der Wahrheit");
    shell.setAlpha((int) (255.0 / 1.0625));
  }
  
  @Override
  protected Control createContents(Composite parent)
  {
    parent.setLayout(new FillLayout());
    
    Table table = new Table(parent, SWT.V_SCROLL | SWT.BORDER);
    table.setHeaderVisible(true);
    table.setLinesVisible(true);
    
    tableViewer = new TableViewer(table);
    createColumns(tableViewer);
    tableViewer.setContentProvider(new ArrayContentProvider());
    
    Object[] rounds = new Object[scores.getMaxRoundCount()];
    for (int i = 0; i < rounds.length; i++)
      rounds[i] = i + 1;
    tableViewer.setInput(rounds);
    
    table.pack();
    getShell().pack();
    
    return table;
  }
  
  protected void createColumns(TableViewer viewer)
  {
    // Runde
    {
      TableViewerColumn v = new TableViewerColumn(tableViewer, SWT.NONE);
      v.getColumn().setText("#");
      v.getColumn().setResizable(false);
      v.getColumn().setWidth(25);
      
      v.setLabelProvider(new ColumnLabelProvider()
      {
        @Override
        public String getText(Object element)
        {
          return element.toString();
        }
      });
    }
    
    // Spieler
    for (int nr = 1; nr <= scores.getPlayerCount(); nr++)
    {
      WizardPlayer player = scores.getPlayer(nr);
      
      TableViewerColumn v = new TableViewerColumn(tableViewer, SWT.CENTER);
      v.getColumn().setText(player.getName());
      v.getColumn().setResizable(false);
      v.getColumn().setWidth(100);
      
      v.setLabelProvider(new ColumnLabelProvider()
      {
        @Override
        public String getText(Object element)
        {
          if (scores.getRoundCount() >= (Integer) element)
          {
            Round round = scores.getRound((Integer) element).get(player);
            
            if (round != null && round.getScore() != null)
            {
              return round.getScore().toString();
            }
          }
          return "";
        }
        
        @Override
        public Color getForeground(Object element)
        {
          if (scores.getRoundCount() >= (Integer) element)
          {
            Round round = scores.getRound((Integer) element).get(player);
            
            if (round != null && round.isWin() != null)
            {
              if (round.isWin())
                return getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN);
              else
                return getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
            }
          }
          return null;
        }
      });
    }
  }
  
  @Subscribe
  public void onShuffle(DeckShuffeledEvent event)
  {
    WorldKit.UI.async(getContents(), () -> tableViewer.refresh());
  }
  
  @Subscribe
  public void onGameOver(GameOverEvent event)
  {
    WorldKit.UI.async(getContents(), () -> tableViewer.refresh());
  }
}