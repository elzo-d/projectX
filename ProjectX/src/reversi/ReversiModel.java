package reversi;

import java.util.ArrayList;

import gamehandler.EndReason;
import gamehandler.GameModel;
import gamehandler.Move;
import gamehandler.Turn;

public class ReversiModel extends GameModel {
	
	private int[] board;
	private Turn turn;
	private EndReason reason;
	private String endInfo;
	private int startingPlayer;
	
	public ReversiModel() {
		board = new int[64];
		turn = Turn.PLAYER1;
		board[8 * 3 + 3] = Turn.PLAYER2.getPieceNum();
		board[8 * 3 + 4] = Turn.PLAYER1.getPieceNum();
		board[8 * 4 + 3] = Turn.PLAYER1.getPieceNum();
		board[8 * 4 + 4] = Turn.PLAYER2.getPieceNum();
	}
	
	@Override
	public void initGame(int startingPlayer) {
		this.startingPlayer = startingPlayer;
		if(startingPlayer == 2) {
			board[8 * 3 + 3] = Turn.PLAYER1.getPieceNum();
			board[8 * 3 + 4] = Turn.PLAYER2.getPieceNum();
			board[8 * 4 + 3] = Turn.PLAYER2.getPieceNum();
			board[8 * 4 + 4] = Turn.PLAYER1.getPieceNum();
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
		if(move.getAsInt(8) < 0 || move.getAsInt(8) >= 64) {
			return false;
		}
		ArrayList<Move> moves = handleTurn(move, turn);
		if(moves == null) {
			return false;
		}
		if(turn == Turn.PLAYER2) {
			player1.tellMove(move);
		} else {
			player2.tellMove(move);
		}
		for(Move m : moves) {
			board[m.getAsInt(8)] = turn.getPieceNum();
		}
		swapTurn();
		if(!movePossible(turn)) {
			// the next player can't move, switch back to this player
			swapTurn();
			if(!movePossible(turn)) {
				// this player can't either, game is done
				finishGame();
				return true;
			}
		}
		if(turn == Turn.PLAYER1) {
			player1.requestMove();
		} else {
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
		return 8;
	}
	
	public int[] getBoard() {
		return board;
	}
	
	public int getStartingPlayer() {
		return startingPlayer;
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
		for(int i = 0; i < 64; i++) {
			counts[board[i]]++;
		}
		return counts;
	}
	
	private void finishGame() {
		int[] counts = getPieceCount();
		if(counts[1] > counts[2]) {
			endGame(EndReason.WIN1, "");
		} else if(counts[2] > counts[1]) {
			endGame(EndReason.WIN2, "");
		} else {
			endGame(EndReason.DRAW, "");
		}
	}
	
	private void swapTurn() {
		if(turn == Turn.PLAYER1) {
			turn = Turn.PLAYER2;
		} else {
			turn = Turn.PLAYER1;
		}
	}
	
	private boolean movePossible(Turn turn) {
		for(int i = 0; i < 64; i++) {
			if(handleTurn(Move.getFromInt(8, i), turn) != null) {
				return true;
			}
		}
		return false;
	}
	
	private ArrayList<Move> handleTurn(Move move, Turn turn) {
		// if piece is already full, not a valid move
		if(board[move.getAsInt(8)] != 0) {
			return null;
		}
		boolean allow = false;
		ArrayList<Move> changes = new ArrayList<>();
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				// for each of the 8 cells around this cell, if it is out of range, check next cell
				if(move.getX() + i < 0 || move.getX() + i >= 8) {
					continue;
				}
				if(move.getY() + j < 0 || move.getY() + j >= 8) {
					continue;
				}
				Move cell = new Move(move.getX() + i, move.getY() + j);
				// if it is our piece, or empty (also skips middle cell), check next cell
				if((board[cell.getAsInt(8)] == turn.getPieceNum()) || board[cell.getAsInt(8)] == 0) {
					continue;
				}
				boolean canDo = false;
				int t = 2;
				// check in each direction from the other player's piece
				while(inRange(move.getX() + (i * t)) && inRange(move.getY() + (j * t))) {
					Move tCell = new Move(move.getX() + (i * t), move.getY() + (j * t));
					// if it is empty, break (move not possible)
					if(board[tCell.getAsInt(8)] == 0) {
						break;
					}
					// else, break but indicate that move is possible
					if(board[tCell.getAsInt(8)] == turn.getPieceNum()) {
						canDo = true;
						break;
					}
					t++;
				}
				// if we can move
				if(canDo) {
					int u = 1;
					// add all other player's pieces to the change-list, these get turned
					while(inRange(move.getX() + (i * u)) && inRange(move.getY() + (j * u))) {
						Move tCell = new Move(move.getX() + (i * u), move.getY() + (j * u));
						// if we hit our piece, break
						if(board[tCell.getAsInt(8)] == turn.getPieceNum()) {
							break;
						}
						changes.add(tCell);
						u++;
					}
					allow = true;
				}
			}
		}
		// if we could move, add the piece we placed to the change-list
		if(allow) {
			changes.add(move);
			return changes;
		}
		// else return null (couldn't move)
		return null;
	}
	
	private boolean inRange(int val) {
		if(val < 0 || val >= 8) {
			return false;
		}
		return true;
	}

}
