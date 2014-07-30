package connect.four.gui;

import static java.awt.Color.GRAY;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import connect.four.game.Game;
import connect.four.game.Game.GameEvent;
import connect.four.game.Game.GameListener;

public class GameWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private BufferStrategy strategy;

	private final Game game;
	private final GameListener gameListener = new GameListenerImp();

	private final int fieldSize = 50;
	private final int drawOffset = 5;
	private final int borderSize = 10;

	public GameWindow(final Game game) {
		this.game = game;
		initialize();
	}
	
	@Override
	public void paint(Graphics graphics) {
		int boardWidth = game.getBoardWidth();
		int boardHeight = game.getBoardHeight();
		int boardPanelHeight = contentPane.getHeight();
		
		if (strategy != null) {
			Graphics2D graphics2D = (Graphics2D) strategy.getDrawGraphics();
			
			graphics2D.setColor(GRAY);
			graphics2D.fillRect(0, 0, getWidth(), getHeight());
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			for (int row = 0; row < boardHeight; row++) {
				for (int column = 0; column < boardWidth; column++) {
					int x = borderSize + column * (drawOffset + fieldSize);
					int y = borderSize + boardPanelHeight - (row * (fieldSize + drawOffset)) - fieldSize;
					int width = fieldSize;
					int height = fieldSize;
					
					graphics2D.setColor(game.getColor(column, row));
					if (game.isEmpty(column, row))
						graphics2D.drawOval(x, y, width, height);
					else
						graphics2D.fillOval(x, y, width, height);
				}
			}
			
			graphics2D.dispose();
			strategy.show();
		}
	}
	
	private void initialize() {
		setTitle("Connect four dots to win");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);

		contentPane = new JPanel();
		contentPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent event) {
				int column = calculateColumn(event);
				
				if (game.canMove(column))
					game.makeNextMove(column, gameListener);
			}
		});
		setContentPane(contentPane);
		
		setVisible(true);
		setWindowSize();
		
		createBufferStrategy(2);
		strategy = getBufferStrategy();
		
		repaintBoard();
	}
	
	private int calculateColumn(MouseEvent event) {
		return max(min((event.getX() - borderSize) / (drawOffset + fieldSize), game.getBoardWidth()-1), 0);
	}

	private void repaintBoard() {
		paint(getGraphics());
	}
	
	private int getBorderX() {
		return getWidth() - getContentPane().getWidth();
	}
	
	private int getBorderY() {
		return getHeight() - getContentPane().getHeight();
	}
	
	private void setWindowSize() {
		int width = getBorderX() + fieldSize * game.getBoardWidth() + (game.getBoardWidth()-1) * drawOffset + 2 * borderSize;
		int height = getBorderY() + fieldSize * game.getBoardHeight() + (game.getBoardHeight()-1) * drawOffset + 2 * borderSize;
		setBounds(getX(), getY(), width, height);
	}
	
	private class GameListenerImp implements GameListener {
		@Override
		public void gameStartEvent(GameEvent endGameEvent) {
			repaintBoard();
		}						
		
		@Override
		public void commonGameEvent(GameEvent endGameEvent) {							
			repaintBoard();
		}
		
		@Override
		public void gameEndEvent(GameEvent endGameEvent) {
			repaintBoard();
			JOptionPane.showMessageDialog(contentPane,
					endGameEvent.getCurrentPlayer().getDescription() + " has won",
					"Game over",
					INFORMATION_MESSAGE);
		}
	}
}