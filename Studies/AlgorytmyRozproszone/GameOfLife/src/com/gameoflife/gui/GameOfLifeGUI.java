package com.gameoflife.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;

import com.gameoflife.model.Board;
import com.gameoflife.model.GameChangeListener;

public final class GameOfLifeGUI implements GameChangeListener {
	private final JFrame window;
	private final Board board;
	
	private final int boardWidthPx;
	private final int boardHeightPx;
	
	public GameOfLifeGUI(final Board board) {
		this.window = new JFrame(Settings.Title);
		this.window.add(new GameOfLifeFrame());
		this.window.setBounds(0, 0, Settings.Width, Settings.Height);
		this.window.setResizable(false);
		this.window.addWindowListener(new GameOfLifeWindowEventHandler());
		
		this.board = board;
		this.board.addListener(this);
		
		this.boardWidthPx  = board.getWidth() * Settings.CellSize;
		this.boardHeightPx = board.getHeight() * Settings.CellSize;
	}

	@Override
	public void gameStarted(Board board) {
		window.setVisible(true);
		window.repaint();
	}

	@Override
	public void gameStoped(Board board) {
		window.setVisible(false);
	}

	@Override
	public void statusChanged(Board board) {
		window.repaint();
	}
	
	@Override
	public void roundChanged(Board board) {
		window.repaint();
	}
	
	@SuppressWarnings("serial")
	private class GameOfLifeFrame extends JComponent {
		@Override
		public void paint(Graphics graphics) {
			super.paint(graphics);
			
			Rectangle bounds 			= graphics.getClipBounds();
			Image canvas 				= createImage(boardWidthPx, boardHeightPx);
			Graphics2D canvasGraphics	= (Graphics2D) canvas.getGraphics();
			
			synchronized (board) {
				for (int x = 0; x < board.getWidth(); x++)
					for (int y = 0; y < board.getHeight(); y++) {
						Color color = chooseColor(board.getAliveSince(x, y));
						canvasGraphics.setColor(color);
						int cellX = x * Settings.CellSize;
						int cellY = y * Settings.CellSize;
						canvasGraphics.fillRect(cellX, cellY, Settings.CellSize, Settings.CellSize);
					}
			}
			
			final int xBegin = bounds.x + (bounds.width  - boardWidthPx)  / 2;
			final int yBegin = bounds.y + (bounds.height - boardHeightPx) / 2;
			graphics.clearRect(bounds.x, bounds.y, bounds.width, bounds.height);
			graphics.drawImage(canvas, xBegin, yBegin, this);
		}
		
		Color chooseColor(long aliveSince) {
			if (aliveSince <=  0L * Settings.ColorGroup) return Settings.DeadCell;
			if (aliveSince <=  1L * Settings.ColorGroup) return Settings.AliveCell1;
			if (aliveSince <=  2L * Settings.ColorGroup) return Settings.AliveCell2;
			if (aliveSince <=  3L * Settings.ColorGroup) return Settings.AliveCell3;
			if (aliveSince <=  4L * Settings.ColorGroup) return Settings.AliveCell4;
			if (aliveSince <=  5L * Settings.ColorGroup) return Settings.AliveCell5;
			if (aliveSince <=  6L * Settings.ColorGroup) return Settings.AliveCell6;
			if (aliveSince <=  7L * Settings.ColorGroup) return Settings.AliveCell7;
			if (aliveSince <=  8L * Settings.ColorGroup) return Settings.AliveCell8;
			if (aliveSince <=  9L * Settings.ColorGroup) return Settings.AliveCell9;
			if (aliveSince <= 10L * Settings.ColorGroup) return Settings.AliveCell10;
			if (aliveSince <= 11L * Settings.ColorGroup) return Settings.AliveCell11;
			if (aliveSince <= 12L * Settings.ColorGroup) return Settings.AliveCell12;
			if (aliveSince <= 13L * Settings.ColorGroup) return Settings.AliveCell13;
			if (aliveSince <= 14L * Settings.ColorGroup) return Settings.AliveCell14;
			if (aliveSince <= 15L * Settings.ColorGroup) return Settings.AliveCell15;
			if (aliveSince <= 16L * Settings.ColorGroup) return Settings.AliveCell16;
														 return Settings.AliveCell17;
		}
	}
	
	private class GameOfLifeWindowEventHandler extends WindowAdapter {
		public void windowClosing(WindowEvent evt) {
		    board.stopGame();
		}   
	}
}
