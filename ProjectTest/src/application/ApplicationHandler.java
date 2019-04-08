package application;

import java.util.HashMap;

import gamehandler.*;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import reversi.ReversiAI;
import reversi.ReversiModel;
import reversi.ReversiView;
import tictactoe.TicTacToeAI;
import tictactoe.TicTacToeModel;
import tictactoe.TicTacToeView;

public class ApplicationHandler {

	private Connection connection;
	// global view, gamemodel, players, view
	private GameModel model;
	private GamePlayer player1;
	private GamePlayer player2;
	private GameView gameView;

	private String username;

	public ApplicationHandler(Stage primaryStage) {

		// make window
		VBox game = new VBox();
		// TEMP: Create gameview here
		gameView = new TicTacToeView();
		game.getChildren().add(gameView.getBoardView());
		game.getChildren().add(gameView.getStatsPane());

		Scene scene = new Scene(game);
		primaryStage.setScene(scene);
		primaryStage.setTitle("TicTacToe");

		primaryStage.show();

		setServer("a", "localhost:7789");
		connection.challengePlayer("b", "Tic-tac-toe");
	}

	public void setServer(String name, String address) {
		username = name;
		connection = new Connection(address.split(":")[0], Integer.parseInt(address.split(":")[1]), this);
		connection.login(name);
	}

	public void requestPlayerList() {
		if(connection == null) {
			return;
		} else {
			connection.getGamelist();
		}
	}

	public void startGame(int beginningPlayer) {
		setUpGame("Tic-tac-toe", "Real", "Remote");
		model.initGame(beginningPlayer);
	}

	public void setUpGame(String game, String playerType1, String playerType2) {
		switch(game) {
			case "Reversi":
				model = new ReversiModel();
				if(playerType1.equals("AI")) {
					player1 = new GamePlayer() {
						@Override
						public void handleClick(Move move) {

						}

						@Override
						public void requestMove(Move move) {

						}

						@Override
						public void endGame() {

						}
					};
				}
				if(playerType2.equals("AI")) {
					player2 = new ReversiAI(2);
				}
				gameView = new ReversiView();
				break;
			case "Tic-tac-toe":
				model = new TicTacToeModel();
				if(playerType1.equals("AI")) {
					player1 = new TicTacToeAI();
				}
				if(playerType2.equals("AI")) {
					player2 = new TicTacToeAI();
				}
				// TEMP: made in constructor
				//gameView = new TicTacToeView();
				break;
			default:
				throw new IllegalArgumentException("Unknown Game-type");
		}
		if(!playerType1.equals("AI")) {
			player1 = getPlayerForType(playerType1, 1);
		}
		if(!playerType2.equals("AI")) {
			player2 = getPlayerForType(playerType2, 2);
		}
		model.setPlayer1(player1);
		model.setPlayer2(player2);
		model.setView(gameView);
		gameView.setModel(model);
		gameView.setPlayer(player1);
		player1.setModel(model);
		player2.setModel(model);
	}

	public void recieveMessage(HashMap<String, Object> map) {
		//send to view
		String type = (String) map.get("MESSAGETYPE");
		switch (type) {
			case "MATCH":
				//System.out.println(type);
				if(map.get("PLAYERTOMOVE").equals(username)) {
					startGame(1);
				} else {
					startGame(2);
				}
				break;
			case "YOURTURN":
				//System.out.println(type);
				((RemotePlayer) player2).onMessage(map);
				break;
			case "MOVE":
				//System.out.println(type);
				if(!map.get("PLAYER").equals(username)) {
					((RemotePlayer) player2).onMessage(map);
				}
				break;
			case "WIN":
				System.out.println(type);
				break;
			case "LOSS":
				System.out.println(type);
				break;
			case "DRAW":
				System.out.println(type);
				break;
			case "CHALLENGE":
				System.out.println(type);
				break;
			case "CHALLENGE_CANCELLED":
				System.out.println(type);
				break;
			case "PLAYERLIST":
				System.out.println(type);
				break;
			case "GAMELIST":
				System.out.println(type);
				break;
			default:
				break;
		}
	}

	private GamePlayer getPlayerForType(String type, int number) {
		switch(type) {
			case "Real":
				return new RealPlayer(number);
			case "Remote":
				return new RemotePlayer(number, connection);
			default:
				throw new IllegalArgumentException("Unknown Player-type");
		}
	}
}
