package connectPD;

import java.util.*;

public class RanDumbPlayer implements ConnectFourPlayer {
  
  Random r;
  public RanDumbPlayer()
  {
    r = new Random();
  }
  
  public int getMove(String[] board, char toMove)
  {
    int i = r.nextInt(7);
    while(board[0].charAt(i)!='.')
      i = r.nextInt(7);
    return i;
  }
}