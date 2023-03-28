package connectPD;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ColemanConnorPlayer implements ConnectFourPlayer{
  
  private int MAX_INT = 1000;
  private int MIN_INT = -1000;
  
  private int ROW = 6;
  private int COL = 7;
  
  /*public class Node{
    ArrayList<Node> children = new ArrayList<Node>();
    int eval;
    
    public void addChild(Node child){
      this.children.add(child);
    }
    
    public Node getChild(int index) {
      return this.children.get(index);
    }
    
  }*/
  
  public int getMove(String[] board, char toMove)
  {
    //board will have 6 strings, each of which has 7 characters
    //The characters will be 'R' for a red disk, 'B' for a black
    //disk, and '.' for no disk.  Each string represents a row
    //of a valid ConnectFour gameBoard.  The first row is the top
    //row, the second row is the second from top, and so on.
    //There will be no 'R' or 'B' characters in a column above a
    //'.' character.
    
    //toMove is 'R' if it is the red player's move, and 'B' if
    //it is the black player's move.
	  
    
    // create a copy of the current board state for minimax algorithm
    //String[] boardCpy = cpyBoard(board);
    
    int colChoices[] = {0, 1, 2, 3, 4, 5, 6};
    int evalChoices[] = {MIN_INT-1, MIN_INT-1, MIN_INT-1, MIN_INT-1, MIN_INT-1, MIN_INT-1, MIN_INT-1};
    int choice = 0;
    
    char notToMove = 'R';
    
    if (toMove == 'R') {
      notToMove = 'B';
    }
    
    
    for (int i = 0; i < COL; i++) {
      // see if a column is full or not
      if (board[0].charAt(i) == '.') {
        Boolean stop = false;
        for (int j = ROW - 1; j >= 0 && !stop; j--) {
          // "drop" the player color on the column
          // start at the bottom and go up rows until you hit a '.'
          if (board[j].charAt(i) == '.') {
            // update the board with the new "token"
            String[] newBoard = cpyBoard(board);
            // replace the '.' at i with the player's character
            newBoard[j] = board[j].substring(0, i) + toMove + board[j].substring(i + 1, board[j].length());
            stop = true;
            evalChoices[i] = minimax(newBoard, 3, false, MIN_INT, MAX_INT, notToMove);
          }
          /*else {
            evalChoices[i] = MIN_INT - 100;
            stop = true;
          }*/
        }
      }
    }
    
    /*for (int i = 0; i < COL; i++) {
      evalChoices[i] = minimax(board, 1, true, MIN_INT, MAX_INT, toMove);
    }*/
    
    Random r = new Random();
    for (int i = 0; i < COL; i++) {
      if (evalChoices[i] == MIN_INT) {
        choice = i;
        break;
      }
      else if (evalChoices[i] == MAX_INT) {
        choice = i;
        break;
      }
      else if (evalChoices[i] != MIN_INT - 100) {
        if (evalChoices[i] > evalChoices[choice]) {
          choice = i;
        }
        else if (evalChoices[i] == evalChoices[choice]) {
          if (r.nextInt(2) == 0) {
            choice = i;
          }
        }
      }
    }
    
    return colChoices[choice];

    
	  //int moveCol = minimax(board, 4, true);
	  
  }
  
  public String[] cpyBoard(String[] board) {
    String[] boardCpy = Arrays.copyOf(board, board.length, String[].class);
    
    /*for (int i = 0; i < board.length; i++) {
      boardCpy[i] = board[i];
    }*/
    
    return boardCpy;
  }
  
  public int heuristic (String[] board, char player) {

    int score = 0;
    char notPlayer = 'R';
    
    if (player == 'R') {
      notPlayer = 'B';
    }
    
    // if the last row is empty, the board is empty
    // the best move is the center if the board is empty
    if(board[5].equals("..." + notPlayer + "...")) {
        return MAX_INT;
    }
    
    else {
      
      /* 1. test for win condition first
         2. if you opponent has a winning move, take the move so he can’t take it
         3. Take the center square over edges and corners
         4. Take corner squares over edges.
         5. Take edges if they are the only thing available
      */ 
      
      // check the rows
      score = 0;
      for(int row = 0; row < ROW; row++) {
        int connection = 1;
        for (int col = 0; col < COL -1; col++) {
          // if the 
          if((board[row].charAt(col) != '.') && board[row].charAt(col + 1) == board[row].charAt(col)) {
            connection++;
          }
          else {
            connection = 1;
          }
          
          if(connection > 1) {
            if(board[row].charAt(col + 1) == player) {
              if (connection == 4) {
                return MAX_INT;
              }
              else if (connection == 3) {
                //if (Math.abs(score) <= Math.abs(MAX_INT - 25)) {
                  score = MAX_INT - 25;
                //}
              }
              else if (connection == 2) {
                //if (Math.abs(score) <= Math.abs(MAX_INT - 50)) {
                  score = MAX_INT - 50;
                //}
              }
              /*else {
                score += Math.pow(2, connection);
              }*/
            }
            else if (board[row].charAt(col + 1) == notPlayer){
              if (connection == 4) {
                return MIN_INT;
              }
              else if (connection == 3) {
                //if (Math.abs(score) < Math.abs(MIN_INT + 25)) {
                  score = MIN_INT + 25;
                //}
              }
              else if (connection == 2) {
                //if (Math.abs(score) < Math.abs(MIN_INT + 50)) {
                  score = MIN_INT + 50;
                //}
              }
              /*else {
                score -= Math.pow(2, connection);
              }*/
            }
          }
        }
 
      }
      
   // check the columns
      //score = 0;
      for(int col = 0; col < COL; col++) {
        int connection = 1;
        for (int row = 0; row < ROW - 1; row++) {
          if(board[row].charAt(col) != '.' && board[row + 1].charAt(col) == board[row].charAt(col)) {
            connection++;
          }
          else {
            connection = 1;
          }
          
          if(connection > 1) {
            if(board[row + 1].charAt(col) == player) {
              if (connection == 4) {
                return MAX_INT;
              }
              else if (connection == 3) {
                //if (Math.abs(score) <= Math.abs(MAX_INT - 25)) {
                  score = MAX_INT - 25;
                //}
              }
              else if (connection == 2) {
                //if (Math.abs(score) <= Math.abs(MAX_INT - 50)) {
                  score = MAX_INT - 50;
                //}
              }
            }
            else if (board[row + 1].charAt(col) == notPlayer){
              if (connection == 4) {
                return MAX_INT;
              }
              else if (connection == 3) {
                //if (Math.abs(score) < Math.abs(MIN_INT + 25)) {
                  score = MIN_INT + 25;
                //}
              }
              else if (connection == 2) {
                //if (Math.abs(score) < Math.abs(MIN_INT + 50)) {
                  score = MIN_INT + 50;
                //}
              }
            }
          }
        }
      }
      
      // check the top left diagonals
      for (int row = 0; row < ROW - 3; row++) {
        int connection = 1;
        for (int col = 0; col < COL - 3; col++) {
          if(board[row].charAt(col) != '.' && board[row + 1].charAt(col + 1) == board[row].charAt(col)) {
            connection++;
          }
          else {
            connection = 1;
          }
          
          if(connection > 1) {
            if(board[row + 1].charAt(col + 1) == player) {
              if (connection == 4) {
                return MAX_INT;
              }
              else if (connection == 3) {
                //if (Math.abs(score) <= Math.abs(MAX_INT - 25)) {
                  score = MAX_INT - 25;
                //}
              }
              else if (connection == 2) {
                //if (Math.abs(score) <= Math.abs(MAX_INT - 50)) {
                  score = MAX_INT - 50;
                //}
              }
            }
            else if (board[row + 1].charAt(col + 1) == notPlayer){
              if (connection == 4) {
                return MAX_INT;
              }
              else if (connection == 3) {
                //if (Math.abs(score) < Math.abs(MIN_INT + 25)) {
                  score = MIN_INT + 25;
                //}
              }
              else if (connection == 2) {
                //if (Math.abs(score) < Math.abs(MIN_INT + 50)) {
                  score = MIN_INT + 50;
                //}
              }
            }
          }
        }
      }
      
   // check the top right diagonals
      for (int row = 3; row < ROW - 1; row++) {
        int connection = 1;
        for (int col = 0; col < COL - 3; col++) {
          if(board[row].charAt(col) != '.' && board[row - 1].charAt(col + 1) == board[row].charAt(col)) {
            connection++;
          }
          else {
            connection = 1;
          }
          
          if(connection > 1) {
            if(board[row - 1].charAt(col + 1) == player) {
              if (connection == 4) {
                return MAX_INT;
              }
              else if (connection == 3) {
                //if (Math.abs(score) <= Math.abs(MAX_INT - 25)) {
                  score = MAX_INT - 25;
                //}
              }
              else if (connection == 2) {
                //if (Math.abs(score) <= Math.abs(MAX_INT - 50)) {
                  score = MAX_INT - 50;
                //}
              }
            }
            else if (board[row - 1].charAt(col + 1) == notPlayer){
              if (connection == 4) {
                return MAX_INT;
              }
              else if (connection == 3) {
                //if (Math.abs(score) < Math.abs(MIN_INT + 25)) {
                  score = MIN_INT + 25;
                //}
              }
              else if (connection == 2) {
                //if (Math.abs(score) < Math.abs(MIN_INT + 50)) {
                  score = MIN_INT + 50;
                //}
              }
            }
          }
        }
      }      
    }
    return score;
  }
  
  public int minimax (String[] board, int depth, Boolean maxPlayer, int alpha, int beta, char player) {
    int eval = 0;
    char notPlayer = 'R';
    int retCol = 0;
    
    //int colChoices[] = {0, 1, 2, 3, 4, 5, 6};
    //int evalChoices[] = {0, 0, 0, 0, 0, 0, 0};
    //int choice = 0;
    
    if (player == 'R') {
      notPlayer = 'B';
    }
    
    //Random r = new Random();
    
    int currBoardEval = heuristic(board, player);
    //System.out.println(currBoardEval);
    
    // MAX_INT = we won
    // MIN_INT = they won
    // 0 = tie game
    
    if (depth == 0 || currBoardEval == MAX_INT || currBoardEval == MIN_INT || board[0].indexOf(".") == -1) { // OR if no more children
		  return currBoardEval;
	  }
    
	  else if (maxPlayer) {
		  eval = MIN_INT; // set to smallest number
		  
		  // for each column if it can be played on (for each child)
		  for (int i = 0; i < COL; i++) {
	      // see if a column is full or not
	      if (board[0].charAt(i) == '.') {
	        Boolean stop = false;
	        for (int j = ROW - 1; j >= 0 && !stop; j--) {
	          // "drop" the player color on the column
	          // start at the bottom and go up rows until you hit a '.'
	          if (board[j].charAt(i) == '.') {
	            // update the board with the new "token"
	            String[] newBoard = cpyBoard(board);
	            // replace the '.' at i with the player's character
	            newBoard[j] = board[j].substring(0, i) + player + board[j].substring(i + 1, board[j].length());
	            stop = true;
	            
	            // evaluate the new board
	            if (player == 'R') {
	              eval = Math.max(eval, minimax(newBoard, depth-1, !maxPlayer, alpha, beta, 'B'));

	            }
	            else {
	              eval = Math.max(eval, minimax(newBoard, depth-1, !maxPlayer, alpha, beta, 'R'));
	            }
	            
	            if (eval >= beta) {
                break;
              }	            
	            alpha = Math.max(alpha, eval); // use the first return value

	          }
	        }
	      }
	      else {
	        eval = MIN_INT - 100;
	      }
		  }
		  
		  return eval;
		  
	  }
	  
	  else {
	    eval = MAX_INT;
	    
	    for (int i = 0; i < COL; i++) {
        // see if a column is full or not
        if (board[0].charAt(i) == '.') {
          Boolean stop = false;
          for (int j = ROW - 1; j >= 0 && !stop; j--) {
            // "drop" the player color on the column
            // start at the bottom and go up rows until you hit a '.'
            if (board[j].charAt(i) == '.') {
              // update the board with the new "token"
              String[] newBoard = cpyBoard(board);
              // replace the '.' at i with the player's character
              newBoard[j] = board[j].substring(0, i) + player + board[j].substring(i + 1, board[j].length());
              stop = true;
              
              // evaluate the new board
              if (player == 'R') {
                eval = Math.min(eval, minimax(newBoard, depth-1, !maxPlayer, alpha, beta, 'B')); 
              }
              else {
                eval = Math.min(eval, minimax(newBoard, depth-1, !maxPlayer, alpha, beta, 'R'));
              }
              
              if (eval <= alpha) {
                break;
              }
              beta = Math.min(beta, eval);
              
            }
          }
        }
        else {
          eval = MAX_INT + 100;
        }
	    }
	  }
    return eval;
  }  
}