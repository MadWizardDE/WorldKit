package one.koslowski.connect4.api;

public class IllegalMoveException extends Exception
{
  private static final long serialVersionUID = 1L;
  
  int x;
  
  IllegalMoveException(int x)
  {
    this.x = x;
  }
}