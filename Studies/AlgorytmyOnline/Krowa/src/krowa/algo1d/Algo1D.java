package krowa.algo1d;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;

import javax.swing.JPanel;

public class Algo1D {
	private final static Random RANDOM = new Random();
	private final static int THICKNESS = 10;
	private final static int COW_LEFT_RANGE = -30;
	private final static int COW_RIGHT_RANGE = 30;
	
	private final int startDirection;
	private final int cowPosition;
	
	private int step;
	
	public Algo1D(StartDirection startDirection) {
		this.startDirection = startDirection.getStartDirection();
		this.step = -1;
		this.cowPosition = generateCowLocation();
	}
	
	public void makeNextStep() {
		step++;
	}
	
	public boolean isCowReached() {
		for (int i = 0; i <= step; i++) {
			int reachedPosition = getFarmerPositionForStep(i);
			if (reachedPosition > 0) {
				if (0 <= cowPosition && cowPosition <= reachedPosition)
					return true;
			} else {
				if (reachedPosition <= cowPosition && cowPosition <= 0)
					return true;				
			}
		}
			
		return false;
	}
	
	public double getCowDistance() {
		return getDistnce(0, cowPosition);
	}
	
	public double getFarmerDistance() {
		double distance = 0;
		for (int i = 0; i <= step; i++)
			distance += getDistnce(getFarmerPositionForStep(i), getFarmerPositionForStep(i-1));
		return distance;
	}
	
	public void drawNextStep(JPanel panel) {
		reset(panel);
		for (int i = 0; i <= step; i++)
			drawSingleStep(panel, i);
		drawCow(panel);
		drawFarmer(panel);
	}
	
	private void reset(JPanel panel) {
		Dimension bounds = panel.getSize();
		Graphics graphics = panel.getGraphics();
		graphics.setColor(WHITE);
		graphics.fillRect(0, 0, bounds.width, bounds.height);
	}
	
	private void drawSingleStep(JPanel panel, int step) {
		int farmersPosition = getFarmerPositionForStep(step);
		
		Dimension bounds = panel.getSize();
		int centerX = (int) bounds.width / 2;
		int centerY = (int) bounds.height / 2;
		
		int edgeX = centerX + farmersPosition * THICKNESS;
		int edgeY = centerY;
		
		drawLine(panel.getGraphics(), centerX, centerY, edgeX, edgeY);
	}
	
	private void drawLine(Graphics graphics, int centerX, int centerY, int edgeX, int edgeY) {
		int width;
		int x;
		if (centerX <= edgeX) {
			width = edgeX-centerX + THICKNESS;
			x = centerX - THICKNESS/2;
		} else {
			width = centerX-edgeX + THICKNESS;			
			x = centerX + THICKNESS/2 - width;
		}
		
		int y;
		int height;
		if (centerY <= edgeY) {
			height = edgeY-centerY + THICKNESS;
			y  = centerY - THICKNESS/2;
		} else {
			height = centerY-edgeY + THICKNESS;
			y  = centerY + THICKNESS/2 - height;			
		}
		
		graphics.setColor(BLACK);
		graphics.fillRect(x, y, width, height);
	}
	
	private void drawCow(JPanel panel) {
		Dimension bounds = panel.getSize();
		int centerX = (int) bounds.width / 2;
		int centerY = (int) bounds.height / 2;
		
		int x = centerX + cowPosition * THICKNESS;
		int y = centerY;
				
		Graphics graphics = panel.getGraphics();
		graphics.setColor(RED);
		graphics.fillRect(x - THICKNESS/2, y - THICKNESS/2, THICKNESS, THICKNESS);
	}
	
	private void drawFarmer(JPanel panel) {
		Dimension bounds = panel.getSize();
		int centerX = (int) bounds.width / 2;
		int centerY = (int) bounds.height / 2;
		
		int x = centerX + getFarmerPositionForStep(step) * THICKNESS;
		int y = centerY;
		
		Graphics graphics = panel.getGraphics();
		graphics.setColor(BLUE);
		graphics.fillRect(x - THICKNESS/2, y - THICKNESS/2, THICKNESS, THICKNESS);
	}
	
	private int getFarmerPositionForStep(int step) {
		if (step < 0)
			return 0;
		int maxPosition = ((step + startDirection) % 2 == 0 ? 1 : -1) * (1 << step);
		return	cowPosition < 0 && maxPosition < 0 ? max(cowPosition, maxPosition) :
				cowPosition > 0 && maxPosition > 0 ? min(cowPosition, maxPosition) :
				maxPosition;
	}
	
	private static int generateCowLocation() {
		int position = 0;
		while (position == 0)
			position = RANDOM.nextInt(COW_RIGHT_RANGE - COW_LEFT_RANGE + 1) + COW_LEFT_RANGE;
		return position;
	}
	
	private int getDistnce(int x1, int x2) {
		return abs(x1 - x2);
	}
}
