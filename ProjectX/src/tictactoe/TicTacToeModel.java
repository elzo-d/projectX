package tictactoe;

import gamehandler.EndReason;
import gamehandler.GameModel;
import gamehandler.Move;
import gamehandler.Turn;

public class TicTacToeModel extends GameModel {
	
	private int[] board;
	private Turn turn;
	private EndReason reason;
	private String endInfo;
	
	public TicTacToeModel() {
		board = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
		turn = Turn.PLAYER1;
	}
	
	@Override
	public void initGame(int startingPlayer) {
		if(startingPlayer == 2) {
			player2.requestMove();
			turn = Turn.PLAYER2;
		} else {
			player1.requestMove();
		}
		view.update();
	}
	
	@Override
	public synchronized boolean doMove(Move move) {
		if(turn == Turn.ENDED) {
			return false;
		}
		if(move.getAsInt(3) < 0 || move.getAsInt(3) >= 9) {
			return false;
		}
		if(board[move.getAsInt(3)] != 0) {
			return false;
		}
		if(turn == Turn.PLAYER2) {
			player1.tellMove(move);
		} else {
			player2.tellMove(move);
		}
		board[move.getAsInt(3)] = turn.getPieceNum();
		if(checkWin()) {
			if(turn == Turn.PLAYER2) {
				endGame(EndReason.WIN2, "");
			} else {
				endGame(EndReason.WIN1, "");
			}
			return true;
		} else if(checkFull()) {
			endGame(EndReason.DRAW, "");
			return true;
		}
		if(turn == Turn.PLAYER2) {
			turn = Turn.PLAYER1;
			player1.requestMove();
		} else {
			turn = Turn.PLAYER2;
			player2.requestMove();
		}
		view.update();
		return true;
	}

	@Override
	public void endGame(EndReason reason, String endInfo) {
		if(turn != Turn.ENDED) {
			player1.endGame();
			player2.endGame();
			turn = Turn.ENDED;
		}
		this.reason = reason;
		this.endInfo = endInfo;
		view.update();
	}
	
	@Override
	public int getWidth() {
		return 3;
	}
	
	public int[] getBoard() {
		return board;
	}
	
	public int[] getStats() {
		int[] values = new int[] {0, 0, 0};
		int[] counts = getPieceCount();
		values[0] = counts[1];
		values[1] = counts[2];
		values[2] = turn.getPieceNum();
		return values;
	}
	
	public String getEndReason() {
		if(reason != null) {
			String add = "";
			if(!endInfo.equals("")) {
				add = " (" + endInfo + ")";
			}
			return reason.getNiceString() + add;
		}
		return "";
	}
	
	private int[] getPieceCount() {
		int[] counts = new int[] {0, 0, 0};
		for(int i = 0; i < 9; i++) {
			counts[board[i]]++;
		}
		return counts;
	}
	
	private boolean checkWin() {
		if(board[0] == board[1] && board[1] == board[2] && board[0] != 0) {
			return true;
		}
		if(board[3] == board[4] && board[4] == board[5] && board[3] != 0) {
			return true;
		}
		if(board[6] == board[7] && board[7] == board[8] && board[6] != 0) {
			return true;
		}
		if(board[0] == board[3] && board[3] == board[6] && board[0] != 0) {
			return true;
		}
		if(board[1] == board[4] && board[4] == board[7] && board[1] != 0) {
			return true;
		}
		if(board[2] == board[5] && board[5] == board[8] && board[2] != 0) {
			return true;
		}
		if(board[0] == board[4] && board[4] == board[8] && board[0] != 0) {
			return true;
		}
		if(board[2] == board[4] && board[4] == board[6] && board[2] != 0) {
			return true;
		}
		return false;
	}
	
	private boolean checkFull() {
		boolean full = true;
		for(int i = 0; i < board.length; i++) {
			if(board[i] == 0) {
				full = false;
			}
		}
		return full;
	}

}
