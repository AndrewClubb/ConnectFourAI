package connectUI;

import connectPD.*;

public class ConnectFourDriver {
  public static void main(String[] s)
  {
    ConnectFourPlayer[] players = {new ColemanConnorPlayer(), new ClubbAndrewPlayer()};
    //ConnectFourPlayer[] players = {new ClubbAndrewPlayer(), new ColemanConnorPlayer()};
    
    String[] board = {".......",
                      ".......",
                      ".......",
                      ".......",
                      ".......",
                      "......."};
    int move = 0;
    while(winner(board) == '.')
    {
      makeMove(board,players[move].getMove(board,move%2==0?'R':'B'),move%2==0?'R':'B');
      move = (move+1)%2;
      System.out.println("");
      for(int i = 0; i < board.length; i++)
        System.out.println(board[i]);
    }
    char c = winner(board);
    if(c=='X') System.out.println("It's a tie");
    else System.out.println(c+" wins");
  }
  
  public static char winner(String[] board)
  {
    for(int i = 0; i < board.length; i++) //horizontal win
      for(int j = 0; j < board[i].length() - 3; j++)
        if(board[i].charAt(j)!='.' && 
           board[i].charAt(j)==board[i].charAt(j+1) &&
           board[i].charAt(j+1)==board[i].charAt(j+2) &&
           board[i].charAt(j+2)==board[i].charAt(j+3))
           return board[i].charAt(j);
    for(int i = 0; i < board.length-3; i++)
    {
      for(int j = 0; j < board[i].length(); j++) //vertical win
        if(board[i].charAt(j)!='.' && 
           board[i].charAt(j)==board[i+1].charAt(j) &&
           board[i+1].charAt(j)==board[i+2].charAt(j) &&
           board[i+2].charAt(j)==board[i+3].charAt(j))
           return board[i].charAt(j);
      for(int j = 0; j < board[i].length()-3; j++) //tlbr
        if(board[i].charAt(j)!='.' && 
           board[i].charAt(j)==board[i+1].charAt(j+1) &&
           board[i+1].charAt(j+1)==board[i+2].charAt(j+2) &&
           board[i+2].charAt(j+2)==board[i+3].charAt(j+3))
           return board[i].charAt(j);
      for(int j = 3; j < board[i].length(); j++) //trbl
        if(board[i].charAt(j)!='.' && 
           board[i].charAt(j)==board[i+1].charAt(j-1) &&
           board[i+1].charAt(j-1)==board[i+2].charAt(j-2) &&
           board[i+2].charAt(j-2)==board[i+3].charAt(j-3))
           return board[i].charAt(j);
    }
    return board[0].indexOf(".")!=-1?'.':'X';
  }
  
  public static void makeMove(String[] board, int col, char player)
  {
    int row = 0;
    while(row < board.length && board[row].charAt(col) == '.') row++;
    row--;
    if(row < 0) { System.err.println("invalid move"); System.exit(1); }
    board[row] = board[row].substring(0,col) 
                 + player + board[row].substring(col+1);
  }
}