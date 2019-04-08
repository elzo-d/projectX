package tictactoe;

import gamehandler.GamePlayer;
import gamehandler.Move;

public class TicTacToeAI extends GamePlayer {
	
	private boolean running;
	private boolean moveRequested;
	
	class AiThread implements Runnable {
		public void run() {
			while(running) {
				if(moveRequested) {
					moveRequested = false;
					while(true) {
						int[] board = ((TicTacToeModel) model).getBoard();
						
						int x = (int) (Math.random() * 3);
						int y = (int) (Math.random() * 3);
						Move tryMove = new Move(x, y);
						if(model.doMove(tryMove)) {
							break;
						}
					}
				} else {
					try {
						Thread.sleep(10);
					} catch(InterruptedException e) {
						System.out.println("AI thread failed to sleep:");
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public TicTacToeAI(int importedPlayerNum) {
		super(importedPlayerNum);
		running = true;
		moveRequested = false;
		Thread aiThread = new Thread(new AiThread());
		aiThread.start();
	}
	
	@Override
	public void handleClick(Move move) {
		// clicks are ignored for AI's
	}

	@Override
	public void requestMove() {
		moveRequested = true;
	}

	@Override
	public void endGame() {
		// stop thread
		running = false;
	}

	@Override
	public void tellMove(Move move) {
		// AI doesn't do anything with the other's move
		
	}

}
