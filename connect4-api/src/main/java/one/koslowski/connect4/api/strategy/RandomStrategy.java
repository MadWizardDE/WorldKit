package one.koslowski.connect4.api.strategy;

import java.util.Random;

import one.koslowski.connect4.api.Connect4Board;
import one.koslowski.connect4.api.Connect4Player.Connect4PlayerStrategy;
import one.koslowski.connect4.api.Connect4World;

public class RandomStrategy implements Connect4PlayerStrategy
{
  private Random random = new Random();

  @Override
  public int move()
  {
    Connect4Board board = Connect4World.getContext().getBoard();

    int move = random.nextInt(board.getWidth());

    return !board.isFull(move) ? move : move();
  }
}