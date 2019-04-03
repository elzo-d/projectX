package reversi;

import gamehandler.GameView;
import gamehandler.Move;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ReversiView extends GameView {
	private Rectangle[] cells;
	private Label stats;
	
	public ReversiView() {
		boardView = new Pane();
		boardView.setPrefSize(400, 400);
		cells = new Rectangle[64];
		for(int i = 0; i < cells.length; i++) {
			final int x = i % 8;
			final int y = i / 8;
			cells[i] = new Rectangle(x * 50, y * 50, 50, 50);
			cells[i].setFill(Color.GREEN);
			cells[i].setOnMouseClicked(e -> {
				handleClick(new Move(x, y));
			});
			boardView.getChildren().add(cells[i]);
		}
		stats = new Label("Stats");
		statsPane = new Pane();
		statsPane.getChildren().add(stats);
	}
	
	@Override
	public void update() {
		String stateString = ((ReversiModel) model).getStateString();
		Platform.runLater(() -> {
			stats.setText(stateString);
			updateBoard();
		});
	}
	
	private void updateBoard() {
		// TODO: Should getBoard and getStateString be part of the GameView?
		int[] board = ((ReversiModel) model).getBoard();
		int startingPlayer = ((ReversiModel) model).getStartingPlayer();
		
		for(int i = 0; i < board.length; i++) {
			int value = board[i];
			if(startingPlayer == 1) {
				if(value == 1) {
					cells[i].setFill(Color.BLACK);
				} else if(value == 2) {
					cells[i].setFill(Color.WHITE);
				} else {
					cells[i].setFill(Color.GREEN);
				}
			} else {
				if(value == 1) {
					cells[i].setFill(Color.WHITE);
				} else if(value == 2) {
					cells[i].setFill(Color.BLACK);
				} else {
					cells[i].setFill(Color.GREEN);
				}
			}
		}
	}
}
