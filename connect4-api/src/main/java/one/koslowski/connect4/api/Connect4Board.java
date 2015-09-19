package one.koslowski.connect4.api;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.ArrayUtils;

import one.koslowski.world.api.Entity;

public class Connect4Board extends Entity
{
  private static final long serialVersionUID = 1L;
  
  public static final int DEFAULT_WIDTH = 7, DEFAULT_HEIGHT = 6;
  
  private Connect4Player[][] board;
  
  /** [(x|y)] */
  Stack<Move> history;
  
  {
    history = new Stack<>();
  }
  
  public Connect4Board()
  {
    this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
  }
  
  public Connect4Board(int width, int height)
  {
    board = new Connect4Player[width][height];
  }
  
  public int getWidth()
  {
    return board.length;
  }
  
  public int getHeight()
  {
    return board[0].length;
  }
  
  public int getSize()
  {
    return getWidth() * getHeight();
  }
  
  public int getCount()
  {
    int count = 0;
    for (int x = 0; x < board.length; x++)
      count += getCount(x);
    return count;
  }
  
  public int getCount(int x)
  {
    return board[x].length - ArrayUtils.lastIndexOf(board[x], null) - 1;
  }
  
  public int getMovesLeft()
  {
    return getWidth() * getHeight() - getCount();
  }
  
  public boolean isFull()
  {
    return getCount() == (getSize());
  }
  
  public boolean isFull(int index)
  {
    return getCount(index) == getHeight();
  }
  
  public boolean isEmpty()
  {
    return getCount() == 0;
  }
  
  public boolean isEmpty(int x)
  {
    return getCount(x) == 0;
  }
  
  public Connect4Player get(int x, int y)
  {
    return board[x][y];
  }
  
  Connect4Player getLine(int count)
  {
    for (int x = 0; x < getWidth(); x++)
      for (int y = 0; y < getHeight(); y++)
        for (Axis axis : Axis.ALL)
          if (axis.count(board, x, y) >= count)
            return board[x][y];
            
    return null;
  }
  
  int play(Connect4Player player, int x) throws IllegalMoveException
  {
    if (x < 0 || x >= board.length)
      throw new IllegalMoveException(x);
      
    int y = ArrayUtils.lastIndexOf(board[x], null);
    
    if (y == -1)
      throw new IllegalMoveException(x);
      
    board[x][y] = player;
    
    // Zug speichern
    history.push(new Move(player, x, y));
    
    return y;
  }
  
  Move undo() throws EmptyStackException
  {
    Move move = history.pop();
    
    board[move.x][move.y] = null;
    
    return move;
  }
  
  void clear()
  {
    board = new Connect4Player[getWidth()][getHeight()];
    
    history.clear();
  }
  
  class Move
  {
    Connect4Player player;
    
    int x, y;
    
    Move(Connect4Player player, int x, int y)
    {
      this.player = player;
      
      this.x = x;
      this.y = y;
    }
  }
  
  private enum Axis
  {
    HORIZONTAL(1, 0), VERTICAL(0, 1), DIAGONAL1(1, 1), DIAGONAL2(1, -1);
    
    public static Axis[] ALL = { HORIZONTAL, VERTICAL, DIAGONAL1, DIAGONAL2 };
    
    private Vector[] vectors;
    
    private Axis(int x, int y)
    {
      vectors = new Vector[] { new Vector(x, y), new Vector(-x, -y) };
    }
    
    public int count(Object[][] matrix, int x, int y)
    {
      int count = 0;
      Object object = matrix[x][y];
      if (object != null)
      {
        count++;
        for (Vector vector : vectors)
        {
          int times = 1;
          while (object.equals(vector.carry(matrix, x, y, times++)))
            count++;
        }
      }
      return count;
    }
    
    @SuppressWarnings("unused")
    public List<int[]> getFreeCoord(Object[][] matrix, int x, int y)
    {
      Object object = matrix[x][y];
      
      List<int[]> coord = new ArrayList<int[]>();
      marker: for (Vector vector : vectors)
      {
        int times = 1;
        Object position;
        while ((position = vector.carry(matrix, x, y, times)) != null)
        {
          times++;
          if (position != object)
            continue marker;
        }
        int x1 = x + times * vector.x;
        int y1 = y + times * vector.y;
        if (x1 > -1 && y1 > -1 && x1 < matrix.length && y1 < matrix[0].length)
          coord.add(new int[] { x1, y1 });
      }
      return coord;
    }
    
    private static class Vector
    {
      public int x, y;
      
      public Vector(int x, int y)
      {
        this.x = x;
        this.y = y;
      }
      
      public Object carry(Object[][] matrix, int x, int y, int times)
      {
        try
        {
          return matrix[x + this.x * times][y + this.y * times];
        }
        catch (IndexOutOfBoundsException e)
        {
          return null;
        }
      }
    }
  }
}