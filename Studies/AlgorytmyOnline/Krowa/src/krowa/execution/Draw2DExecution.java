package krowa.execution;

import static java.lang.Thread.sleep;
import static javax.swing.JOptionPane.showMessageDialog;
import static javax.swing.SwingUtilities.invokeLater;

import javax.swing.JPanel;

import krowa.algo2d.Algo2D;
import krowa.gui.MainWindow2D;

public class Draw2DExecution implements Runnable {
	private final MainWindow2D window;
	
	public Draw2DExecution(MainWindow2D window) {
		this.window = window;
	}

	@Override
	public void run() {
		invokeLater(new Runnable() {
			@Override
			public void run() {				
				window.setEnabled(false);
			}
		});	
		
		JPanel drawingPanel = window.getPanel();
		
		Algo2D algo = new Algo2D(window.getRotationDirection(), window.getStartDirection());
		algo.drawNextStep(drawingPanel);
		
		while (!algo.isCowReached()) {
			algo.makeNextStep();
			algo.drawNextStep(drawingPanel);
			try {
				sleep(150);
			} catch (InterruptedException e) {
			}
		}
		
		showMessageDialog(
				window.getFrame(),
				"Optimal distance: " + algo.getCowDistance() + "\n" +
				"Farmer's distance: " + algo.getFarmerDistance() + "\n" +
				"Ratio: " + algo.getFarmerDistance()/algo.getCowDistance());
		
		invokeLater(new Runnable() {
			@Override
			public void run() {				
				window.setEnabled(true);
			}
		});	
	}
}
