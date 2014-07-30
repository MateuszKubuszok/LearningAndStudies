package krowa.algo2d;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Random;

import javax.swing.JPanel;

public class Algo2D {
	private final static Random RANDOM = new Random();
	private final static int THICKNESS = 30;
	private final static int COW_LEFT_RANGE = -6;
	private final static int COW_RIGHT_RANGE = 6;
	private final static int COW_TOP_RANGE = -6;
	private final static int COW_BOTTOM_RANGE = 6;
	
	private final RotationDirection rotationDirection;
	private final StartDirection startDirection;
	private final Dimension cowPosition;
	
	private int step;
	
	public Algo2D(RotationDirection rotationDirection, StartDirection startDirection) {
		this.rotationDirection = rotationDirection;
		this.startDirection = startDirection;
		this.step = -1;
		this.cowPosition = generateCowLocation();
	}
	
	public void makeNextStep() {
		step++;
	}
	
	public boolean isCowReached() {
		Dimension currentStepPosition = getFarmerMaxPositionForStep(step);
		Dimension previousStepPosition = getFarmerMaxPositionForStep(step-1);
		if (cowPosition.height == currentStepPosition.height) {
			if (currentStepPosition.height == previousStepPosition.height) {
				int lower = min(currentStepPosition.width, previousStepPosition.width);
				int upper = max(currentStepPosition.width, previousStepPosition.width);
				if (lower <= cowPosition.width && cowPosition.width <= upper)
					return true;
			}	
		}
		if (cowPosition.width == currentStepPosition.width) {
			if (currentStepPosition.width == previousStepPosition.width) {
				int lower = min(currentStepPosition.height, previousStepPosition.height);
				int upper = max(currentStepPosition.height, previousStepPosition.height);
				if (lower <= cowPosition.height && cowPosition.height <= upper)
					return true;
			}	
		}
			
		return false;
	}
	
	public double getCowDistance() {
		return getDistance(new Dimension(), cowPosition);
	}
	
	public double getFarmerDistance() {
		double distance = 0;
		for (int i = 0; i <= step; i++)
			distance += getDistance(getFarmerPositionForStep(i), getFarmerPositionForStep(i-1));
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
		Dimension currentFarmerPosition = getFarmerPositionForStep(step);
		Dimension previousFarmerPosition = getFarmerPositionForStep(step-1);
		
		Dimension bounds = panel.getSize();
		int centerX = (int) bounds.width / 2;
		int centerY = (int) bounds.height / 2;
		
		int startX = centerX + previousFarmerPosition.width * THICKNESS;
		int startY = centerY + previousFarmerPosition.height * THICKNESS;
		
		int endX = centerX + currentFarmerPosition.width * THICKNESS;
		int endY = centerY + currentFarmerPosition.height * THICKNESS;
		
		drawLine(panel.getGraphics(), startX, startY, endX, endY);
	}
	
	private void drawLine(Graphics graphics, int startX, int startY, int endX, int endY) {
		int width;
		int x;
		if (startX <= endX) {
			width = endX-startX + THICKNESS;
			x = startX - THICKNESS/2;
		} else {
			width = startX-endX + THICKNESS;			
			x = startX + THICKNESS/2 - width;
		}
		
		int y;
		int height;
		if (startY <= endY) {
			height = endY-startY + THICKNESS;
			y  = startY - THICKNESS/2;
		} else {
			height = startY-endY + THICKNESS;
			y  = startY + THICKNESS/2 - height;			
		}
		
		graphics.setColor(BLACK);
		graphics.fillRect(x, y, width, height);
	}
	
	private void drawCow(JPanel panel) {
		Dimension bounds = panel.getSize();
		int centerX = (int) bounds.width / 2;
		int centerY = (int) bounds.height / 2;
		
		int x = centerX + cowPosition.width * THICKNESS;
		int y = centerY + cowPosition.height * THICKNESS;
				
		Graphics graphics = panel.getGraphics();
		graphics.setColor(RED);
		graphics.fillRect(x - THICKNESS/2, y - THICKNESS/2, THICKNESS, THICKNESS);
	}
	
	private void drawFarmer(JPanel panel) {
		Dimension bounds = panel.getSize();
		int centerX = (int) bounds.width / 2;
		int centerY = (int) bounds.height / 2;
		
		Dimension farmerPosition = getFarmerPositionForStep(step);
		int x = centerX + farmerPosition.width * THICKNESS;
		int y = centerY + farmerPosition.height * THICKNESS;
		
		Graphics graphics = panel.getGraphics();
		graphics.setColor(BLUE);
		graphics.fillRect(x - THICKNESS/2, y - THICKNESS/2, THICKNESS, THICKNESS);
	}
	
	private Dimension getFarmerPositionForStep(int step) {
		Dimension currentStepPosition = getFarmerMaxPositionForStep(step);
		Dimension previousStepPosition = getFarmerMaxPositionForStep(step-1);
		
		if (cowPosition.height == currentStepPosition.height) {
			if (currentStepPosition.height == previousStepPosition.height) {
				int lower = min(currentStepPosition.width, previousStepPosition.width);
				int upper = max(currentStepPosition.width, previousStepPosition.width);
				if (lower <= cowPosition.width && cowPosition.width <= upper)
					return cowPosition;
			}	
		} else if (cowPosition.width == currentStepPosition.width) {
			if (currentStepPosition.width == previousStepPosition.width) {
				int lower = min(currentStepPosition.height, previousStepPosition.height);
				int upper = max(currentStepPosition.height, previousStepPosition.height);
				if (lower <= cowPosition.height && cowPosition.height <= upper)
					return cowPosition;
			}	
		}	
		
		return currentStepPosition;
	}
	
	private Dimension getFarmerMaxPositionForStep(int step) {
		int y = getFarmerMaxYPositionForStep(step);
		int x = getFarmerMaxYPositionForStep(step - 1);
		return startDirection.transform(rotationDirection.transform(new Dimension(x,y)));
	}
	
	private int getFarmerMaxYPositionForStep(int step) {
		if (step >= 0)
			return (step / 4 + 1) * (step % 4 >= 2 ? -1 : 1);
		return 0;
	}
	
	private Dimension generateCowLocation() {
		Dimension position = new Dimension();
		while (getDistance(new Dimension(), position) == 0.0)
			position =  new Dimension(
				RANDOM.nextInt(COW_RIGHT_RANGE - COW_LEFT_RANGE + 1) + COW_LEFT_RANGE,
				RANDOM.nextInt(COW_BOTTOM_RANGE - COW_TOP_RANGE + 1) + COW_TOP_RANGE
			);
		return position;
	}
	
	private double getDistance(Dimension p1, Dimension p2) {
		int dx = p1.width - p2.width;
		int dy = p1.height - p2.height;
		return sqrt(dx*dx + dy*dy);
	}
}
