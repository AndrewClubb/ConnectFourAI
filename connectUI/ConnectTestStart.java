package connectUI;

import connectPD.ClubbAndrewPlayer;

public class ConnectTestStart {

	public static void main(String[] args) {
		ClubbAndrewPlayer player = new ClubbAndrewPlayer();
	    String[] board = 
	    	   {"...B...",
                "...B...",
                "...B...",
                "..RRR..",
                "..RRB..",
                "RRRBB.R"};
		
		System.out.println(player.getMove(board, 'B'));

	}

}
