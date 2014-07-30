package connect.four.game;

import static connect.four.game.EPlayer.COMPUTER;
import static connect.four.game.EPlayer.HUMAN;
import static connect.four.game.EPlayer.NO_ONE;

import java.awt.Color;

public class Game {
	private Board board;
	private AI computer;
	private EPlayer currentPlayer;
	
	public Game() {
		board = new Board(7, 6);
		startNewGame();
	}
	
	public Game(int columns, int rows) {
		board = new Board(columns, rows);
		startNewGame();
	}
	
	public boolean canMove(int column) {
		return board.isColumnFree(column);
	}
	
	public int getBoardHeight() {
		return board.getHeight();
	}
	
	public int getBoardWidth() {
		return board.getWidth();
	}
	
	public Color getColor(int column, int row) {
		return board.getElement(column, row).getColor();
	}
	
	public boolean isEmpty(int column, int row) {
		return board.getElement(column, row).isEmpty();
	}
	
	private void startNewGame() {
		computer = new AI(board);
		currentPlayer = COMPUTER;
		board.clear();
		board.occupy(computer.computeNextMove(), currentPlayer);
		currentPlayer = HUMAN;
	}
	
	public void makeNextMove(int column, GameListener gameListener) {
		board.occupy(column, currentPlayer);
		
		@SuppressWarnings("unused")
		boolean result = checkDraw(gameListener)
				|| checkWin(gameListener)
				|| notifyHumanMove(gameListener);
	}
	
	private void makeComputersMove(GameListener gameListener) {
		currentPlayer = COMPUTER;
		
		board.occupy(computer.computeNextMove(), currentPlayer);
		
		@SuppressWarnings("unused")
		boolean result = checkDraw(gameListener)
				|| checkWin(gameListener)
				|| notifyComputerMove(gameListener);
	}
	
	private boolean checkDraw(GameListener gameListener) {
		if (board.hasEndedInDraw()) {
			currentPlayer = NO_ONE;
			sendGameEndEvent(gameListener);
			startNewGame();
			sendGameStartEvent(gameListener);
			return true;
		}
		return false;
	}
	
	private boolean checkWin(GameListener gameListener) {
		if (board.isGameEnded(currentPlayer)) {
			sendGameEndEvent(gameListener);
			startNewGame();
			sendGameStartEvent(gameListener);
			return true;
		}
		return false;
	}
	
	private boolean notifyHumanMove(GameListener gameListener) {
		sendCommonGameEvent(gameListener);
		makeComputersMove(gameListener);
		return true;
	}
	
	private boolean notifyComputerMove(GameListener gameListener) {
		currentPlayer = HUMAN;
		sendCommonGameEvent(gameListener);
		return true;
	}
	
	private void sendGameStartEvent(GameListener gameListener) {
		if (gameListener != null)
			gameListener.gameStartEvent(new GameEvent());
	}
	
	private void sendCommonGameEvent(GameListener gameListener) {
		if (gameListener != null)
			gameListener.commonGameEvent(new GameEvent());
	}
	
	private void sendGameEndEvent(GameListener gameListener) {
		if (gameListener != null)
			gameListener.gameEndEvent(new GameEvent());
	}
	
	public class GameEvent {
		public EPlayer getCurrentPlayer() {
			return currentPlayer;
		}
	}
	
	public interface GameListener {
		public void gameStartEvent(GameEvent gameEvent);
		public void commonGameEvent(GameEvent gameEvent);
		public void gameEndEvent(GameEvent gameEvent);
	} 
}
