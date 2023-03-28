package connectPD;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Random;

public class ClubbAndrewPlayer implements ConnectFourPlayer{

	public int getMove(String[] board, char toMove) {		
		Node root = new Node(4, board, toMove, true, -1);
		
		return root.calcHeuristic(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY).getMove();
	}
	
	public void printBoard(String[] board) {
		for(int row = 0; row < 6; row++)
			System.out.println(board[row]);
	}
	
	private class Link {
		private double value;
		private int move;

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

		public int getMove() {
			return move;
		}

		public void setMove(int move) {
			this.move = move;
		}
	}
	
	private class Node {
		private Node[] children;
		private int movesLeft;
		private String[] myBoard;
		private char player;
		private boolean maxPlayer;
		private boolean hasChildren;
		private int myCol;
		
		public Node(int moves, String[] board, char player, boolean maxPlayer, int myCol) {
			myBoard = duplicateBoard(board);
			children = new Node[7];
			movesLeft = moves;
			this.player = player;
			this.maxPlayer = maxPlayer;
			this.myCol = myCol;
			
			if(moves > 0)
				createChildren();
		}
		
		private String[] duplicateBoard(String[] original) {
			String[] newBoard = new String[6];
			
			for(int i = 0; i < 6; i++)
				newBoard[i] = new String(original[i]);
			
			return newBoard;
		}
		
		private void createChildren() {
			hasChildren = false;
			
			for(int i = 0; i < 7; i++)
				children[i] = null;
			
			for(int i = 0; i < 7; i++) {
				if(myBoard[0].charAt(i) == '.') {
					hasChildren = true;
					
					if(player == 'R')
						children[i] = new Node(movesLeft - 1, makeMove(myBoard, i, player), 'B', !maxPlayer, i);
					else
						children[i] = new Node(movesLeft - 1, makeMove(myBoard, i, player), 'R', !maxPlayer, i);
				}					
			}
		}
		
		private String[] makeMove(String[] board, int collumn, char player) {
			int row = 0;
			boolean flag = true;
			
			String[] movedBoard = duplicateBoard(board);
			
			for (int i = 0; i < 6 && flag; i++) {
				row = i;
				if(movedBoard[i].charAt(collumn) != '.') {
					flag = false;
					row -= 1;
				}
			}
			
			if(collumn == 0)
				movedBoard[row] = player + movedBoard[row].substring(1);
			else if (collumn == 6)
				movedBoard[row] = movedBoard[row].substring(0, 6) + player;
			else
				movedBoard[row] = movedBoard[row].substring(0, collumn) + player + movedBoard[row].substring(collumn + 1);
			
			return movedBoard;
		}
		
		public Link calcHeuristic(double alpha, double beta) {
			Link link = new Link();
			double tempValue;
			char otherPlayer;
			
			if(player == 'R')
				otherPlayer = 'B';
			else
				otherPlayer = 'R';
			
			if(!hasChildren) { //Evaluate this board's value
				boolean playerFour = isFourInRow(this.myBoard, this.player), 
						playerThree = isThreeInRow(this.myBoard, this.player), 
						playerTwo = isTwoInRow(this.myBoard, this.player), 
						otherFour = isFourInRow(this.myBoard, otherPlayer),
						otherThree = isThreeInRow(this.myBoard, otherPlayer), 
						otherTwo = isTwoInRow(this.myBoard, otherPlayer);
				
				//Test for win
				if(playerFour)
					link.setValue(40);
				else if(otherFour)
					link.setValue(-40);
				
				//Test for win block
				else if(this.isWinBlocked(this.myBoard, otherPlayer, this.myCol))
					link.setValue(35);
				else if(this.isWinBlocked(this.myBoard, this.player, this.myCol))
					link.setValue(-35);
				
				//Test for 3-in-a-row
				else if(otherThree && playerThree)
					link.setValue(0);
				else if(playerThree && !otherThree && !otherTwo)
					link.setValue(30);
				else if(otherThree && !playerThree && !playerTwo)
					link.setValue(-30);
				else if(playerThree && !otherThree && otherTwo)
					link.setValue(25);
				else if(otherThree && !playerThree && playerTwo)
					link.setValue(-25);
				
				//Test for 2-in-a-row
				else if(playerTwo && otherTwo)
					link.setValue(0);
				else if(playerTwo && !otherTwo)
					link.setValue(20);
				else if(otherTwo && !playerTwo)
					link.setValue(-20);
			}
			else { //Evaluate children and pick min/max
				Random rand = new Random();
				if(maxPlayer) {
					link.setValue(Double.NEGATIVE_INFINITY);
					
					for(int i = 0; i < 7; i++) {
						if(children[i] != null) {
							tempValue = children[i].calcHeuristic(alpha, beta).getValue();
							
							if (tempValue >= beta)
								return link;
							
							if(tempValue > link.getValue()) {
								link.setValue(tempValue);
								link.setMove(i);
							}
							else if(tempValue == link.getValue() && link.getMove() != 3) {
								int originalFromCenter, newFromCenter;
								
								//Find the distance from center for the current best move
								if(link.getMove() >= 3)
									originalFromCenter = link.getMove() - 3;
								else
									originalFromCenter = 3 - link.getMove();
								
								//Find the distance from center for this move
								if(i >= 3)
									newFromCenter = i - 3;
								else
									newFromCenter = 3 - i;
								
								if(newFromCenter < originalFromCenter) //If this move closer to center than original move
									link.setMove(i);
								
								//If both moves are equally distant from the center column, randomly select which to keep
								else if(newFromCenter == originalFromCenter && rand.nextInt(2) == 0) 
									link.setMove(i);
							}
						}
					}
					
					alpha = Math.max(alpha, link.getValue());
				}
				else {
					link.setValue(Double.POSITIVE_INFINITY);
					
					for (int i = 0; i < 7; i++) {
						if (children[i] != null) {
							tempValue = children[i].calcHeuristic(alpha, beta).getValue();
							
							if (tempValue <= alpha)
								return link;
							
							if(tempValue < link.getValue()) {
								link.setValue(tempValue);
								link.setMove(i);
							}
							else if(tempValue == link.getValue() && link.getMove() != 3) {
								int originalFromCenter, newFromCenter;
								
								//Find the distance from center for the current best move
								if(link.getMove() >= 3)
									originalFromCenter = link.getMove() - 3;
								else
									originalFromCenter = 3 - link.getMove();
								
								//Find the distance from center for this move
								if(i >= 3)
									newFromCenter = i - 3;
								else
									newFromCenter = 3 - i;
								
								if(newFromCenter < originalFromCenter) //If this move closer to center than original move
									link.setMove(i);
								
								//If both moves are equally distant from the center column, randomly select which to keep
								else if(newFromCenter == originalFromCenter && rand.nextInt(2) == 0) 
									link.setMove(i);
							}
						}
					}
					
					beta = Math.min(beta, link.getValue());
				}
			}
			return link;
		}
		
		public boolean isWinBlocked(String[] board, char otherChar, int colPlayed) {
			int rowPlayed = -1;
			boolean isBlocked = false;
			
			for(int i = 0; rowPlayed == -1 && i < 6; i++)
				if(board[i].charAt(colPlayed) != '.')
					rowPlayed = i;
			
			//test for vertical block
			if (rowPlayed < 3 && board[rowPlayed + 1].charAt(colPlayed) == otherChar && board[rowPlayed + 2].charAt(colPlayed) == otherChar && board[rowPlayed + 3].charAt(colPlayed) == otherChar)
				isBlocked = true;
			
			//test for horizontal block - center
			else if(!isBlocked && colPlayed == 3 && board[rowPlayed].charAt(colPlayed + 1) == otherChar && board[rowPlayed].charAt(colPlayed + 2) == otherChar && board[rowPlayed].charAt(colPlayed + 3) == otherChar)
				isBlocked = true;
			else if(!isBlocked && colPlayed == 3 && board[rowPlayed].charAt(colPlayed - 1) == otherChar && board[rowPlayed].charAt(colPlayed - 2) == otherChar && board[rowPlayed].charAt(colPlayed - 3) == otherChar)
				isBlocked = true;
			
			//test for horizontal block - left/right
			else if(!isBlocked && colPlayed < 3 && board[rowPlayed].charAt(colPlayed + 1) == otherChar && board[rowPlayed].charAt(colPlayed + 2) == otherChar && board[rowPlayed].charAt(colPlayed + 3) == otherChar)
				isBlocked = true;
			else if(!isBlocked && colPlayed < 4 && colPlayed > 0 && 
					board[rowPlayed].charAt(colPlayed - 1) == otherChar && board[rowPlayed].charAt(colPlayed + 1) == otherChar && board[rowPlayed].charAt(colPlayed + 2) == otherChar)
				isBlocked = true;
			else if(!isBlocked && colPlayed < 5 && colPlayed > 1 && 
					board[rowPlayed].charAt(colPlayed - 2) == otherChar && board[rowPlayed].charAt(colPlayed - 1) == otherChar && board[rowPlayed].charAt(colPlayed + 1) == otherChar)
				isBlocked = true;
			else if(!isBlocked && colPlayed > 2 && board[rowPlayed].charAt(colPlayed - 3) == otherChar && board[rowPlayed].charAt(colPlayed - 2) == otherChar && board[rowPlayed].charAt(colPlayed - 1) == otherChar)
				isBlocked = true;
			
			//test for top left to bottom right block
			else if (!isBlocked && rowPlayed < 3 && colPlayed < 4 && 
					board[rowPlayed + 1].charAt(colPlayed + 1) == otherChar && board[rowPlayed + 2].charAt(colPlayed + 2) == otherChar && board[rowPlayed + 3].charAt(colPlayed + 3) == otherChar)
				isBlocked = true;
			else if (!isBlocked && rowPlayed < 4 && colPlayed < 5 && rowPlayed > 0 && colPlayed > 0 && 
					board[rowPlayed - 1].charAt(colPlayed - 1) == otherChar && board[rowPlayed + 1].charAt(colPlayed + 1) == otherChar && board[rowPlayed + 2].charAt(colPlayed + 2) == otherChar)
				isBlocked = true;
			else if (!isBlocked && rowPlayed < 5 && colPlayed < 6 && rowPlayed > 1 && colPlayed > 1 &&
					board[rowPlayed - 1].charAt(colPlayed - 1) == otherChar && board[rowPlayed - 2].charAt(colPlayed - 2) == otherChar && board[rowPlayed + 1].charAt(colPlayed + 1) == otherChar)
				isBlocked = true;
			else if (!isBlocked && rowPlayed > 2 && colPlayed > 2 &&
					board[rowPlayed - 1].charAt(colPlayed - 1) == otherChar && board[rowPlayed - 2].charAt(colPlayed - 2) == otherChar && board[rowPlayed - 3].charAt(colPlayed - 3) == otherChar)
				isBlocked = true;
			
			//test for top right to bottom left block
			else if (!isBlocked && rowPlayed < 3 && colPlayed > 2 && 
					board[rowPlayed + 1].charAt(colPlayed - 1) == otherChar && board[rowPlayed + 2].charAt(colPlayed - 2) == otherChar && board[rowPlayed + 3].charAt(colPlayed - 3) == otherChar)
				isBlocked = true;
			else if (!isBlocked && rowPlayed < 4 && colPlayed > 1 && rowPlayed > 0 && colPlayed < 6 && 
					board[rowPlayed - 1].charAt(colPlayed + 1) == otherChar && board[rowPlayed + 1].charAt(colPlayed - 1) == otherChar && board[rowPlayed + 2].charAt(colPlayed - 2) == otherChar)
				isBlocked = true;
			else if (!isBlocked && rowPlayed < 5 && colPlayed > 0 && rowPlayed > 1 && colPlayed < 5 &&
					board[rowPlayed - 1].charAt(colPlayed + 1) == otherChar && board[rowPlayed - 2].charAt(colPlayed + 2) == otherChar && board[rowPlayed + 1].charAt(colPlayed - 1) == otherChar)
				isBlocked = true;
			else if (!isBlocked && rowPlayed > 2 && colPlayed < 4 &&
					board[rowPlayed - 1].charAt(colPlayed + 1) == otherChar && board[rowPlayed - 2].charAt(colPlayed + 2) == otherChar && board[rowPlayed - 3].charAt(colPlayed + 3) == otherChar)
				isBlocked = true;
			
			return isBlocked;
		}

		public boolean isFourInRow(String[] board, char boardPlayer) {
			boolean found = false;
			
			for(int row = 0; row < 6 && !found; row++) //horizontal
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row].charAt(col + 1) == boardPlayer && board[row].charAt(col + 2) == boardPlayer && board[row].charAt(col + 3) == boardPlayer)
						found = true;
			
			for(int row = 0; row < 3 && !found; row++) //vertical
				for(int col = 0; col < 7 && !found; col++) 
					if (board[row].charAt(col) == boardPlayer && board[row + 1].charAt(col) == boardPlayer && board[row + 2].charAt(col) == boardPlayer && board[row + 3].charAt(col) == boardPlayer)
						found = true;
			
			for(int row = 0; row < 3 && !found; row++) //diagonal upper left to bottom right
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row + 1].charAt(col + 1) == boardPlayer && board[row + 2].charAt(col + 2) == boardPlayer && board[row + 3].charAt(col + 3) == boardPlayer)
						found = true;
			
			for(int row = 3; row < 6 && !found; row++) //diagonal upper right to bottom left
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row - 1].charAt(col + 1) == boardPlayer && board[row - 2].charAt(col + 2) == boardPlayer && board[row - 3].charAt(col + 3) == boardPlayer)
						found = true;
			
			return found;
		}
		
		public boolean isThreeInRow(String[] board, char boardPlayer) {
			boolean found = false;
			
			//horizontal
			for(int row = 0; row < 6 && !found; row++) //1
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == '.' && board[row].charAt(col + 1) == boardPlayer && board[row].charAt(col + 2) == boardPlayer && board[row].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 0; row < 6 && !found; row++) //2
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row].charAt(col + 1) == '.' && board[row].charAt(col + 2) == boardPlayer && board[row].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 0; row < 6 && !found; row++) //3
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row].charAt(col + 1) == boardPlayer && board[row].charAt(col + 2) == '.' && board[row].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 0; row < 6 && !found; row++) //4
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row].charAt(col + 1) == boardPlayer && board[row].charAt(col + 2) == boardPlayer && board[row].charAt(col + 3) == '.')
						found = true;
			
			//vertical
			for(int row = 0; row < 3 && !found; row++)
				for(int col = 0; col < 7 && !found; col++)
					if (board[row].charAt(col) == '.' && board[row + 1].charAt(col) == boardPlayer && board[row + 2].charAt(col) == boardPlayer && board[row + 3].charAt(col) == boardPlayer)
						found = true;
			
			//diagonal upper left to bottom right
			for(int row = 0; row < 3 && !found; row++) //1
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == '.' && board[row + 1].charAt(col + 1) == boardPlayer && board[row + 2].charAt(col + 2) == boardPlayer && board[row + 3].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 0; row < 3 && !found; row++) //2
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row + 1].charAt(col + 1) == '.' && board[row + 2].charAt(col + 2) == boardPlayer && board[row + 3].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 0; row < 3 && !found; row++) //3
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row + 1].charAt(col + 1) == boardPlayer && board[row + 2].charAt(col + 2) == '.' && board[row + 3].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 0; row < 3 && !found; row++) //4
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row + 1].charAt(col + 1) == boardPlayer && board[row + 2].charAt(col + 2) == boardPlayer && board[row + 3].charAt(col + 3) == '.')
						found = true;
			
			//diagonal upper right to bottom left
			for(int row = 3; row < 6 && !found; row++) //1
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == '.' && board[row - 1].charAt(col + 1) == boardPlayer && board[row - 2].charAt(col + 2) == boardPlayer && board[row - 3].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 3; row < 6 && !found; row++) //2
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row - 1].charAt(col + 1) == '.' && board[row - 2].charAt(col + 2) == boardPlayer && board[row - 3].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 3; row < 6 && !found; row++) //3
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row - 1].charAt(col + 1) == boardPlayer && board[row - 2].charAt(col + 2) == '.' && board[row - 3].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 3; row < 6 && !found; row++) //4
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row - 1].charAt(col + 1) == boardPlayer && board[row - 2].charAt(col + 2) == boardPlayer && board[row - 3].charAt(col + 3) == '.')
						found = true;
			
			return found;
		}
		
		public boolean isTwoInRow(String[] board, char boardPlayer) {
			boolean found = false;
			
			//horizontal
			for(int row = 0; row < 6 && !found; row++) //1-2
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == '.' && board[row].charAt(col + 1) == '.' && board[row].charAt(col + 2) == boardPlayer && board[row].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 0; row < 6 && !found; row++) //1-3
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == '.' && board[row].charAt(col + 1) == boardPlayer && board[row].charAt(col + 2) == '.' && board[row].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 0; row < 6 && !found; row++) //1-4
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == '.' && board[row].charAt(col + 1) == boardPlayer && board[row].charAt(col + 2) == boardPlayer && board[row].charAt(col + 3) == '.')
						found = true;
			for(int row = 0; row < 6 && !found; row++) //2-3
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row].charAt(col + 1) == '.' && board[row].charAt(col + 2) == '.' && board[row].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 0; row < 6 && !found; row++) //2-4
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row].charAt(col + 1) == '.' && board[row].charAt(col + 2) == boardPlayer && board[row].charAt(col + 3) == '.')
						found = true;
			for(int row = 0; row < 6 && !found; row++) //3-4
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row].charAt(col + 1) == boardPlayer && board[row].charAt(col + 2) == '.' && board[row].charAt(col + 3) == '.')
						found = true;
			
			//vertical
			for(int row = 0; row < 3 && !found; row++)
				for(int col = 0; col < 7 && !found; col++)
					if (board[row].charAt(col) == '.' && board[row + 1].charAt(col) == '.' && board[row + 2].charAt(col) == boardPlayer && board[row + 3].charAt(col) == boardPlayer)
						found = true;
			
			//diagonal upper left to bottom right
			for(int row = 0; row < 3 && !found; row++) //1-2
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == '.' && board[row + 1].charAt(col + 1) == '.' && board[row + 2].charAt(col + 2) == boardPlayer && board[row + 3].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 0; row < 3 && !found; row++) //1-3
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == '.' && board[row + 1].charAt(col + 1) == boardPlayer && board[row + 2].charAt(col + 2) == '.' && board[row + 3].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 0; row < 3 && !found; row++) //1-4
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == '.' && board[row + 1].charAt(col + 1) == boardPlayer && board[row + 2].charAt(col + 2) == boardPlayer && board[row + 3].charAt(col + 3) == '.')
						found = true;
			for(int row = 0; row < 3 && !found; row++) //2-3
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row + 1].charAt(col + 1) == '.' && board[row + 2].charAt(col + 2) == '.' && board[row + 3].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 0; row < 3 && !found; row++) //2-4
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row + 1].charAt(col + 1) == '.' && board[row + 2].charAt(col + 2) == boardPlayer && board[row + 3].charAt(col + 3) == '.')
						found = true;
			for(int row = 0; row < 3 && !found; row++) //3-4
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row + 1].charAt(col + 1) == boardPlayer && board[row + 2].charAt(col + 2) == '.' && board[row + 3].charAt(col + 3) == '.')
						found = true;
			
			//diagonal upper right to bottom left
			for(int row = 3; row < 6 && !found; row++) //1-2
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == '.' && board[row - 1].charAt(col + 1) == '.' && board[row - 2].charAt(col + 2) == boardPlayer && board[row - 3].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 3; row < 6 && !found; row++) //1-3
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == '.' && board[row - 1].charAt(col + 1) == boardPlayer && board[row - 2].charAt(col + 2) == '.' && board[row - 3].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 3; row < 6 && !found; row++) //1-4
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == '.' && board[row - 1].charAt(col + 1) == boardPlayer && board[row - 2].charAt(col + 2) == boardPlayer && board[row - 3].charAt(col + 3) == '.')
						found = true;
			for(int row = 3; row < 6 && !found; row++) //2-3
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row - 1].charAt(col + 1) == '.' && board[row - 2].charAt(col + 2) == '.' && board[row - 3].charAt(col + 3) == boardPlayer)
						found = true;
			for(int row = 3; row < 6 && !found; row++) //2-4
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row - 1].charAt(col + 1) == '.' && board[row - 2].charAt(col + 2) == boardPlayer && board[row - 3].charAt(col + 3) == '.')
						found = true;
			for(int row = 3; row < 6 && !found; row++) //3-4
				for(int col = 0; col < 4 && !found; col++)
					if (board[row].charAt(col) == boardPlayer && board[row - 1].charAt(col + 1) == boardPlayer && board[row - 2].charAt(col + 2) == '.' && board[row - 3].charAt(col + 3) == '.')
						found = true;
			
			return found;
		}
	}
}